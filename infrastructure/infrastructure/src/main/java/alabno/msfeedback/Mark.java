package alabno.msfeedback;

public enum Mark {

    ASTAR, APLUS, A, B, C, D, E, F;

    public Double toDouble() {
        switch (this) {
        case ASTAR:
            return 95d;
        case APLUS:
            return 85d;
        case A:
            return 75d;
        case B:
            return 65d;
        case C:
            return 55d;
        case D:
            return 45d;
        case E:
            return 35d;
        case F:
            return 15d;
        default:
            return 0d;
        }
    }
    
    public static Mark fromString(String s) {
        switch (s.toUpperCase()) {
        case "A*":
            return ASTAR;
        case "A+":
            return APLUS;
        default:
            return Mark.valueOf(s.toUpperCase());
        }
    }

    @Override
    public String toString() {
        switch (this) {
        case ASTAR:
            return "A*";
        case APLUS:
            return "A+";
        default:
            return super.toString();
        }
    }

}
