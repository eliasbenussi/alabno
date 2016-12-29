package alabno.wserver;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.java_websocket.WebSocket;
import org.junit.Test;

import alabno.database.DatabaseConnection;
import alabno.database.MySqlDatabaseConnection;
import alabno.database.NullDatabaseConnection;
import alabno.msfeedback.FeedbackUpdaters;
import alabno.userauth.Authenticator;
import alabno.userauth.NullAuthenticator;
import alabno.userauth.TestTokenGenerator;
import alabno.userauth.TokenGenerator;
import alabno.usercapabilities.AllPermissions;
import alabno.usercapabilities.Permissions;

public class WebSocketHandlerTest {

    // Mockeries
    WebSocket mockWebSocketConnection = mock(WebSocket.class);
    ExecutorService mockExecutorService = mock(ExecutorService.class);
    DatabaseConnection mockDatabase = mock(MySqlDatabaseConnection.class);
    DatabaseConnection nullDatabase = new NullDatabaseConnection();
    Authenticator authenticator = new NullAuthenticator();
    TokenGenerator tokenGenerator = new TestTokenGenerator();
    Permissions permissions = new AllPermissions();
    WebSocketHandler handler = new WebSocketHandler(mockExecutorService, new FeedbackUpdaters(), mockDatabase,
            authenticator, tokenGenerator, permissions, false);
    WebSocketHandler handler2 = new WebSocketHandler(mockExecutorService, new FeedbackUpdaters(), nullDatabase,
            authenticator, tokenGenerator, permissions, false);

    @Test
    public void handleMessageEmpty() {

        replay(mockWebSocketConnection);
        replay(mockExecutorService);

        handler.handleMessage(mockWebSocketConnection, "blah");

        verify(mockWebSocketConnection);
        verify(mockExecutorService);
    }

    @Test
    public void handleLoginTest() {

        Capture<String> captured_string = EasyMock.<String>newCapture();

        mockWebSocketConnection.send(EasyMock.capture(captured_string));
        mockWebSocketConnection.send((String) anyObject());

        replay(mockWebSocketConnection);
        replay(mockExecutorService);

        handler.handleMessage(mockWebSocketConnection,
                "    {\r\n        \"type\": \"login\",\r\n        \"username\": \"gj414\",\r\n        \"password\": \"9c4b8a984db84c98b49fa849a8\"\r\n    }");

        verify(mockWebSocketConnection);
        verify(mockExecutorService);

        assertTrue(captured_string.getValue().contains("login_success"));

    }

    @Test
    public void handleNewAssignmentTest() {

        // login
        mockWebSocketConnection.send((String) anyObject()); // receive login
                                                            // success and token
        mockWebSocketConnection.send((String) anyObject()); // receive jobs list
        // submit job
        expect(mockExecutorService.submit(isA(AssignmentCreator.class))).andReturn(null); // job
                                                                                          // is
                                                                                          // submitted
        
        
        mockWebSocketConnection.send((String) anyObject());
        mockWebSocketConnection.send((String) anyObject());
        
        Capture<String> captured_string = EasyMock.<String>newCapture();
        mockWebSocketConnection.send(EasyMock.capture(captured_string)); // success
        
        

        replay(mockExecutorService);
        replay(mockWebSocketConnection);

        handler2.handleMessage(mockWebSocketConnection,
                "    {\r\n        \"type\": \"login\",\r\n        \"username\": \"gj414\",\r\n        \"password\": \"9c4b8a984db84c98b49fa849a8\"\r\n    }");

        handler2.handleMessage(mockWebSocketConnection,
                "{\r\n\"type\": \"new_assignment\",\r\n\"id\": \"gj414-98332052\",\r\n\"title\": \"PINTOS\",\r\n\"ex_type\": \"C\",\r\n\"model_git\": \"https://gitlab.doc.ic.ac.uk/ajf/autumn/haskell_quadratic.git\",\r\n\"students_git\": [\r\n{\"git\":\"https://gitlab.doc.ic.ac.uk/autumn_16_17/haskell_quadratic_gj414.git\",\"uname\":\"foijhapsoier\"},\r\n{\"git\":\"https://gitlab.doc.ic.ac.uk/autumn_16_17/haskell_quadratic_ap2314.git\",\"uname\":\"if\"}\r\n]\r\n}");

        verify(mockWebSocketConnection);
        verify(mockExecutorService);

        System.out.println(captured_string.getValue());
        assertTrue(captured_string.getValue().contains("job_sent"));
    }

    @Test
    public void exerciseTypeListTest() {
        Map<String, String> dbQueryMap1 = new HashMap<String, String>();
        Map<String, String> dbQueryMap2 = new HashMap<String, String>();
        dbQueryMap1.put("type", "abc");
        dbQueryMap2.put("type", "javajava");
        List<Map<String, String>> typesList = new ArrayList<>();
        typesList.add(dbQueryMap1);
        typesList.add(dbQueryMap2);

        expect(mockDatabase.retrieveQueryString(anyObject())).andReturn(typesList);

        Capture<String> capturedString = EasyMock.<String>newCapture();
        mockWebSocketConnection.send(EasyMock.capture(capturedString)); // the
                                                                        // types
                                                                        // message

        // replay ---------------------
        replay(mockWebSocketConnection);
        replay(mockDatabase);

        // test actions ----------------------------------
        handler.sendExerciseTypes(mockWebSocketConnection);

        // verify mocks ---------------
        verify(mockWebSocketConnection);
        verify(mockDatabase);

        String captured = capturedString.getValue();
        assertTrue(captured.contains("abc"));
        assertTrue(captured.contains("javajava"));
        assertTrue(captured.contains("typelist"));
    }
}
