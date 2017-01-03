package alabno.simple_haskell_marker;

import java.util.List;

/**
 * Utility executable to split a haskell file and print it to stdout
 *
 */
public class MainSplitter {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Expecting 1 argument!");
            System.exit(1);
        }
        
        String filePath = args[0];
        
        HaskellSplitter splitter = new HaskellSplitter(filePath);
        
        List<HaskellBlock> blocks = splitter.split();
        
        for (HaskellBlock block : blocks) {
            System.out.println("ok\t" + block.getBlockText());
        }
        
        System.exit(0);
    }
    
}
