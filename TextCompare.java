import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Hashtable;
import java.util.Enumeration;

/**************************************************************************
  * Program: TextCompare 
  * Author: Elliott Bingol - CS1501
  * 
  * Useage: java TextCompare filename_1 filename_2
  * 
  *   This program: 
  *   - reads in two text files 
  *   - distributes each into a hashtable 
  *   - reports statistics about each to user
  *   - uses algorithm to determine similarity of texts; reports to user.
  * 
  * ***********************************************************************/
public class TextCompare {
  public static void main (String[] args) {
    Stopwatch time = new Stopwatch();
    Hashtable<String, Node> f1, f2;
    double distance;
    
    // Checks that number of arguments is EXACTLY 2
    if (args.length != 2) {
      System.out.println("Useage: java TextCompare filename_1.txt filename_2.txt");
      System.exit(0);
    }
    
    // Read in files, distribute into hashtables for comparison
    f1 = importFile(args[0]); 
    f2 = importFile(args[1]); 
    
    // Use the shorter file because faster (all matching words are in both anyway)
    if (f1.size() <= f2.size()) {  
      distance = innerProduct(f1, f2) / ( norm(f1) * norm(f2) );  // Formula for determining similarity
    } else distance = innerProduct(f2, f1) / ( norm(f1) * norm(f2) );
    
    distance = Math.acos(distance); // Answer in radians

    System.out.printf("The distance between the documents is: %.6f radians\n", distance);   
    System.out.println("Time elapsed: " + time.elapsedTime() + " seconds");
  }
  
  /***********************************************************************************************************
    * Method: importFile(String fileName)
    * 
    *   This method checks that the input file exists, then uses a Scanner to read in the file line by line.
    *   Each line is converted into an array of basic words of the same case, and put into a hashtable which
    *   also maintains a record of the word's frequency. 
    * 
    *   A record of the file's number of lines, words, and distinct words are also reported to the user.
    *   
    * RETURNS: Hashtable<String, Node> wordList
    * 
    * *******************************************************************************************************/
  
  private static Hashtable<String, Node> importFile(String fileName) {
    Hashtable<String, Node> wordList = new Hashtable<String, Node>();
    int lines=0, words=0; // Counters
    
    try { // Checks if file exists
      File file = new File(fileName);
      Scanner scan = new Scanner(file);
      String[] splitter;

      while (scan.hasNextLine()) {
        String str = scan.nextLine();
        str = str.replaceAll("[^A-Za-z0-9 ]", " "); // Replaces all non-alphanumeric characters with spaces
        str = str.toLowerCase(); // Converts everything to same case
        splitter = str.split("\\s+"); // Splits into array at spaces
        
        for (int i=0; i < splitter.length; i++) { 
          if (!splitter[i].equals("")) { // Checks and disregards all empty strings
            words++; // Increment word count
            if (wordList.containsKey(splitter[i])) { 
              wordList.get(splitter[i]).increment();   // If key is already in hashtable, increment that key's frequency
            } else wordList.put(splitter[i], new Node(splitter[i])); // Else put in the new key
          }
        }
        
        lines++; // Increment number of lines
      }
      scan.close();
      
    } catch (FileNotFoundException e) {  
      System.out.println(fileName + " is not found!");
    }  
    
    System.out.println("File " + fileName + ": " + lines + " lines, " + words + " words, " + wordList.size() + " distinct words");
    
    return wordList;
  }
  
  /***************************************************************************************************
    * Method: innerProduct(Shorter hashtable, Longer hashtable)
    * 
    *   This method evaluates the innerProduct of the two word lists by iterating through the shorter
    *   of the two data structures, finding all words that exist in both (if a word is in both, then
    *   the one iterated though doesn't matter; shorter is faster).
    * 
    *   Calculates summation of all matching words by multiplying that word's frequency in each file.
    * 
    * RETURNS: double innerProduct
    * 
    * ************************************************************************************************/
  
  public static double innerProduct (Hashtable<String,Node> x, Hashtable<String,Node> y) {
    double innerProduct=0;
    Enumeration<Node> xEnum = x.elements(); // Enumeration of hashtable for iteration
    
    while (xEnum.hasMoreElements()) {
      Node comp = xEnum.nextElement();
      if (y.containsKey(comp.getWord())) {
        Node mirror = y.get(comp.getWord());
        innerProduct += comp.getFrequency()*mirror.getFrequency();     
      }
    }
    
    return innerProduct;
  }
  
  /**********************************************************************************************************
    * Method: norm (hashtable x)
    * 
    *   This method calculates the norm values of one given word list. Iterates through the given hashtable,
    *   squaring every word's frequency and adding it to a running total. 
    * 
    * RETURNS: double norm
    * 
    * *******************************************************************************************************/
  
  public static double norm(Hashtable<String,Node> x) {
    double norm=0;
    Enumeration<Node> xEnum = x.elements(); // Enumeration of hashtable for iteration
    
    while (xEnum.hasMoreElements()) { 
      Node next = xEnum.nextElement();
      norm += Math.pow(next.getFrequency(),2);
    } norm = Math.sqrt(norm);
    
    return norm;
  }
  
  /*****************************************************************************************
    * Class: Stopwatch
    * 
    *   Implementation of Sedgewick's stopwatch in the textbook. Identical.
    * 
    * **************************************************************************************/
  
  private static class Stopwatch {
    private final long start;
    
    public Stopwatch() {
      start = System.currentTimeMillis();
    }
    
    public double elapsedTime() {
      long now = System.currentTimeMillis();
      return (now - start)/1000.0; // Converts to seconds
    }
  }
  
  /**********************************************************************************************************
    * Class: Node(String word)
    * 
    *   Basic word node class. Only variables are the String itself and the frequency of occurence within
    *   the list. Frequency is automatically set to 1 and incremented within the importFile(String fileName)
    *   method. Includes basic getters for word/frequency
    *
    * ******************************************************************************************************/
  
  private static class Node {
    private String word;
    private int frequency;
    
    public Node(String word) {
      this.word = word;
      this.frequency = 1;
    }
    
    public String getWord () {
      return word;
    }
    
    public void increment() {
      frequency++;
    }
    
    public int getFrequency () {
      return frequency;
    }
  }
}