package pegorov.lesson4;

public class Lesson4_1 {

    static Object lock = new Object();
    static volatile char c = 'A';

    public static class Run implements Runnable {
        private char currentSymbol;
        private char nextSymbol;
        private int count;

        public Run(char currentSymbol, char nextSymbol, int count) {
            this.currentSymbol = currentSymbol;
            this.nextSymbol = nextSymbol;
            this.count = count;
         }

        @Override
        public void run() {
            for (int i = 0; i < count; i++) {
                synchronized (lock) {
                    try {
                        while (c != currentSymbol)
                            lock.wait();
                        System.out.print(currentSymbol);
                        c = nextSymbol;
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
        new Thread(new Run('A', 'B', 5)).start();
        new Thread(new Run('B', 'C', 5)).start();
        new Thread(new Run('C', 'A', 5)).start();

    }
}
