import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {

    static BlockingQueue<String> aQueue = new ArrayBlockingQueue<>(100);
    static BlockingQueue<String> bQueue = new ArrayBlockingQueue<>(100);
    static BlockingQueue<String> cQueue = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) throws InterruptedException {

        Thread generation = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                String text = generateText("abc", 100_000);
                try {
                    aQueue.put(text);
                    bQueue.put(text);
                    cQueue.put(text);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        generation.start();

        Thread letterA = new Thread(() -> {
            char letter = 'a';
            int maxA = maxLetter(aQueue, letter);
            System.out.printf("Максимальное количество букв %s - %d шт\n", letter, maxA);
        });

        Thread letterB = new Thread(() -> {
            char letter = 'b';
            int maxB = maxLetter(bQueue, letter);
            System.out.printf("Максимальное количество букв %s - %d шт\n", letter, maxB);
        });

        Thread letterC = new Thread(() -> {
            char letter = 'c';
            int maxC = maxLetter(cQueue, letter);
            System.out.printf("Максимальное количество букв %s - %d шт\n", letter, maxC);
        });

        letterA.start();
        letterB.start();
        letterC.start();

        letterA.join();
        letterB.join();
        letterC.join();
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static int maxLetter(BlockingQueue<String> queue, char letter) {
        int count = 0;
        int max = 0;
        String text;
        try {
            for (int i = 0; i < 10_000; i++) {
                text = queue.take();
                for (char c : text.toCharArray()) {
                    if (letter == c) {
                        count++;
                    }
                }
                if (count > max) {
                    max = count;
                }
            }
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " was interrupted");
            return -1;
        }
        return max;
    }
}