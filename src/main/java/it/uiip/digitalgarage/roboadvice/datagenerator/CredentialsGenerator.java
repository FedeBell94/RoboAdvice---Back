package it.uiip.digitalgarage.roboadvice.datagenerator;

/**
 * Created by fabio on 09/03/2017.
 */
public class CredentialsGenerator {
    final static char firstChar = 'a';

    public static final void main(String[] args) {
        String[] users = GenerateUsers(1000000, 8);
        for (int i = 0; i < 1000000; i++) {
            System.out.println(users[i]);
        }
    }

    static String[] GenerateUsers(int numberOfUsers, int digitNumber) {

        String[] users = new String[numberOfUsers];
        char[] sample = new char[digitNumber];
        for (int i = 0; i < digitNumber; i++) {
            sample[i] = firstChar;
        }
        for (int i = 0; i < numberOfUsers; i++) {
            users[i] = getNext(sample, sample.length - 1);
        }
        return users;
    }

    static String getNext(char[] s, int i) {
        if (s[i] < 'z') {
            if (s[i] == 'Z') s[i] += 7; else s[i]++;
            return new String(s);
        }
        s[i] = firstChar;
        if (i > 0) return getNext(s, i - 1);
        return new String(s);
    }
}