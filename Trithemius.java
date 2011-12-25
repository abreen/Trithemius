/**
 * Trithemius - A tool that implements Johannes Trithemius' simple polyalphabetic cipher
 * Written by Alexander Breen (alexanderbreen.com)
 * v1.0, 25 December 2011
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

import java.util.*;
import java.io.*;

public class Trithemius {
    static char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    static String CIPHER_FROM = "Ciphertext from file or stdin (f/s)? ";
    static String CIPHER_TO = "Ciphertext to file or stdout (f/s)? ";
    static String PLAIN_FROM = "Plaintext from file or stdin (f/s)? ";
    static String PLAIN_TO = "Plaintext to file or stdout (f/s)? ";
    static String DONT_KNOW = "Don't know what '%s' means.";
    static String FILE_ERROR = "File could not be located or cannot be used. Try again.";
    
    public static void main(String[] args) {
        int offset;

        boolean decipher = false;
        Scanner console = new Scanner(System.in);
        
        String inputQuestion;
        String outputQuestion;

        Scanner input = null;
        PrintStream output = null;

        // Argument should be the action
        if (args.length > 0 && (args[0].equalsIgnoreCase("encipher") || args[0].equalsIgnoreCase("decipher"))) {
            if (args[0].equalsIgnoreCase("decipher")) { decipher = true; }
        } else {
            if (args.length > 0 && args[0].equalsIgnoreCase("--help")) {
                System.out.println("Trithemius - A tool that implements Johannes Trithemius' simple polyalphabetic cipher");
                System.out.println("Written by Alexander Breen (alexanderbreen.com)");
                System.out.println("v1.0, 25 December 2011");
                System.out.println("Released under version 3 of the GNU Public License.");
                System.exit(0);
            } else if (args.length > 0) {
                System.out.printf(DONT_KNOW + "\n", args[0]);
            }
            // Ask for the action
            System.out.print("Shall we encipher or decipher (e/d)? ");
            String response = console.next();
            while (!response.equalsIgnoreCase("e") && !response.equalsIgnoreCase("d")) {
                System.out.print("Encipher or decipher (e/d)? ");
                response = console.next();
            }
            if (response.equalsIgnoreCase("d")) { decipher = true; }
        }
        
        if (decipher) {
            inputQuestion = CIPHER_FROM;
            outputQuestion = PLAIN_TO;
        } else {
            inputQuestion = PLAIN_FROM;
            outputQuestion = CIPHER_TO;
        }

        do {
            try {
                input = askForSource(inputQuestion);
            } catch (FileNotFoundException e) {
                System.out.println(FILE_ERROR);
            }
        } while (input == null);

        do {
            try {
                output = askForDestination(outputQuestion);
            } catch (FileNotFoundException e) {
                System.out.println(FILE_ERROR);
            }
        } while (output == null);

        // Ask for offset
        System.out.println("Offsets should be in 0..25; an offset >25 will be modulated.");
        offset = askForOffset("Initial offset: ");
        
        // We now have input/output objects ready for reading/writing
        System.out.println("------");
        
        if (decipher) {
            int numChars = decipher(input, output, offset);
            System.out.println("------");
            System.out.printf("Reached the end. %d characters deciphered.\n", numChars);
        } else {
            int numChars = encipher(input, output, offset);
            System.out.println("------");
            System.out.printf("Reached the end. %d characters enciphered.\n", numChars);
        }
    }

    public static int decipher(Scanner input, PrintStream output, int initialOffset) {
        int currentOffset = initialOffset;
        int numChars = 0;        // Number of characters affected
        
        while (input.hasNextLine()) {

            // Dump this line
            String thisLine = input.nextLine().toLowerCase();

            // Loop through each character
            for (int i = 0; i < thisLine.length(); i++) {

                if (Character.isLetter(thisLine.charAt(i))) {

                    // This character is alpabetical
                    // Find its alphabetical array index
                    int thisIndex = findIndex(thisLine.charAt(i));

                    if (thisIndex >= 0) {
                        // We now have this character's position in our array
                        // Using the current offset, find the decoded position
                        output.print(shiftLetter(currentOffset, thisLine.charAt(i)));
                        currentOffset = (currentOffset+1)%26;
                        numChars++;
                    }

                } else {
                    if (thisLine.charAt(i) == '%') { return numChars; }
                    output.print(thisLine.charAt(i));
                }
            }
            output.println();
        }
        return numChars;
    }

    public static int encipher(Scanner input, PrintStream output, int initialOffset) {
        int currentOffset = initialOffset;
        int numChars = 0;        // Number of characters affected
        
        while (input.hasNextLine()) {

            // Dump this line
            String thisLine = input.nextLine().toLowerCase();

            // Loop through each character
            for (int i = 0; i < thisLine.length(); i++) {
                
                if (Character.isLetter(thisLine.charAt(i))) {

                    // This character is alpabetical
                    // Find its alphabetical array index
                    int thisIndex = findIndex(thisLine.charAt(i));

                    if (thisIndex >= 0) {
                        // We now have this character's position in our array
                        // Using the current offset, find the encoded position
                        output.print(shiftLetter(currentOffset, thisLine.charAt(i)));
                        currentOffset = ((currentOffset-1)+26)%26;
                        numChars++;
                    }

                } else {
                    if (thisLine.charAt(i) == '%') { return numChars; }
                    output.print(thisLine.charAt(i));
                }
            }
            output.println();
        }
        return numChars;
    }

    /* Asks the user for a source, and returns the proper Scanner object. */
    public static Scanner askForSource(String message) throws FileNotFoundException {
        Scanner console = new Scanner(System.in);
        System.out.print(message);
        String response = console.next();
        while (!response.equalsIgnoreCase("f") && !response.equalsIgnoreCase("s")) {
            System.out.printf(DONT_KNOW + "\n%s", response, message);
            response = console.next();
        }
        if (response.equalsIgnoreCase("f")) {
            return new Scanner(askForFile("Path to file: "));
        } else {
            System.out.println("Text will be read line by line from stdin. Use '%' character to halt reading.");
            return console;
        }
    }

    /* Asks the user for a destination, and returns a PrintStream object. */
    public static PrintStream askForDestination(String message) throws FileNotFoundException {
        Scanner console = new Scanner(System.in);
        System.out.print(message);
        String response = console.next();
        while (!response.equalsIgnoreCase("f") && !response.equalsIgnoreCase("s")) {
            System.out.printf(DONT_KNOW + "\n%s", response, message);
            response = console.next();
        }
        if (response.equalsIgnoreCase("f")) {
            return new PrintStream(askForFile("Path to file: "));
        } else {
            return new PrintStream(System.out);
        }
    }

    public static int askForOffset(String message) {
        Scanner console = new Scanner(System.in);
        System.out.print(message);
        while (!console.hasNextInt()) {
            console.next();
            System.out.print("Offset must be an integer.\nInitial offset: ");
        }
        return console.nextInt();
    }

    /* Ask for a file. Returns a valid File object or throws an exception. */
    public static File askForFile(String message) throws FileNotFoundException {
        Scanner console = new Scanner(System.in);
        System.out.print(message);
        File file = new File(console.next());
        
        // Verify that the file exists
        if (file.exists() && file.isFile() && file.canRead() && file.canWrite()) {
            return file;
        } else {
            throw new FileNotFoundException();
        }
    }

    public static char shiftLetter(int byOffset, char c) {
        byOffset = byOffset % 26;
        int thisPosition = findIndex(c);
        return alphabet[((thisPosition+byOffset)%26)];
    }
    
    public static int findIndex(char c) {
        // Loop through the alphabet array to find this character's position
        for (int i = 0; i < alphabet.length; i++) {
            if (alphabet[i] == c) {
                return i;
            }
        }
        return -1;
    }

}