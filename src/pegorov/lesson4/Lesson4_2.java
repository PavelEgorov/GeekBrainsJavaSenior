package pegorov.lesson4;

public class Lesson4_2 {
    static Object lock = new Object();
    static int position = 0;

    public static class Run implements Runnable {
        private char currentSymbol;
        private String world;
        private int countInWord;

        public Run(char currentSymbol, String word) {
            this.world = word;
            this.currentSymbol = currentSymbol;
            this.countInWord = world.length() - world.replace(""+currentSymbol, "").length();
        }

        @Override
        public void run() {
            for (int i = 0; i < 5*countInWord; i++) {
                synchronized (lock) {
                    try {
                        while (this.world.charAt(position) != currentSymbol)
                            lock.wait();
                        System.out.print(currentSymbol);
                        if (position +1 == world.length()){
                            position = 0;
                        }else {
                            position += 1;
                        }
                       lock.notifyAll();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        Object lock = new String();

        new Thread(new Run('A', "AABCC")).start();
        new Thread(new Run('B', "AABCC")).start();
        new Thread(new Run('C', "AABCC")).start();

    }

}
