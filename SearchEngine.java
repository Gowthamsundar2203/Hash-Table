class NGramEntry {

    String ngram;
    int[] docIds = new int[100];
    int count = 0;
    NGramEntry next;

    NGramEntry(String n, int docId) {
        ngram = n;
        docIds[count++] = docId;
        next = null;
    }

    void addDoc(int docId) {
        docIds[count++] = docId;
    }
}

class HashTable {

    NGramEntry[] table;
    int capacity = 1000;

    HashTable() {
        table = new NGramEntry[capacity];
    }

    int hash(String key) {

        int h = 0;

        for (int i = 0; i < key.length(); i++) {
            h = h + key.charAt(i);
        }

        return h % capacity;
    }

    void insert(String ngram, int docId) {

        int index = hash(ngram);

        NGramEntry current = table[index];

        while (current != null) {

            if (current.ngram.equals(ngram)) {
                current.addDoc(docId);
                return;
            }

            current = current.next;
        }

        NGramEntry newEntry = new NGramEntry(ngram, docId);
        newEntry.next = table[index];
        table[index] = newEntry;
    }

    NGramEntry search(String ngram) {

        int index = hash(ngram);

        NGramEntry current = table[index];

        while (current != null) {

            if (current.ngram.equals(ngram))
                return current;

            current = current.next;
        }

        return null;
    }
}

class PlagiarismDetector {

    HashTable table = new HashTable();
    int N = 5;

    String[] extractWords(String text) {
        return text.split(" ");
    }

    String buildNGram(String[] words, int start) {

        String gram = "";

        for (int i = 0; i < N; i++) {
            gram = gram + words[start + i] + " ";
        }

        return gram;
    }

    void addDocument(String text, int docId) {

        String[] words = extractWords(text);

        for (int i = 0; i <= words.length - N; i++) {

            String gram = buildNGram(words, i);
            table.insert(gram, docId);
        }
    }

    void analyzeDocument(String text) {

        String[] words = extractWords(text);

        int total = 0;

        int[] matchCount = new int[100];

        for (int i = 0; i <= words.length - N; i++) {

            String gram = buildNGram(words, i);

            NGramEntry entry = table.search(gram);

            if (entry != null) {

                for (int j = 0; j < entry.count; j++) {
                    matchCount[entry.docIds[j]]++;
                }
            }

            total++;
        }

        for (int i = 0; i < 100; i++) {

            if (matchCount[i] > 0) {

                double similarity = (matchCount[i] * 100.0) / total;

                System.out.println("Found " + matchCount[i] + 
                " matching n-grams with document " + i);

                System.out.println("Similarity: " + similarity + "%");

                if (similarity > 60)
                    System.out.println("PLAGIARISM DETECTED");
            }
        }
    }
}

public class SearchEngine {

    public static void main(String[] args) {

        PlagiarismDetector detector = new PlagiarismDetector();

        String doc1 = "data structures and algorithms are important for computer science students learning programming";

        String doc2 = "algorithms are important for computer science students learning programming concepts";

        detector.addDocument(doc1, 1);

        detector.analyzeDocument(doc2);
    }
}