package dev.dewy.yggdrasil.tests;

import dev.dewy.yggdrasil.models.Game;
import dev.dewy.yggdrasil.models.TokenPair;
import dev.dewy.yggdrasil.sync.SyncYggdrasil;
import dev.dewy.yggdrasil.sync.SyncYggdrasilClient;

import java.util.UUID;

/**
 * A simple example of how to use the **yggdrasil** library with Java.
 *
 * @author dewy
 */
public class JavaExample {
    public static final String USERNAME = "username (email, IGN for unmigrated accounts)";
    public static final String PASSWORD = "password";

    public static void main(String[] args) {
        // With Java, you have to use SyncYggdrasil and SyncYggdrasilClient,
        // unless you want to deal with coroutines shenanigans from Java (not fun).
        TokenPair pair = SyncYggdrasil.authenticate(USERNAME, PASSWORD, Game.MINECRAFT, UUID.randomUUID());

        // It's recommended to use an YggdrasilClient instead of making low-level manual requests using the *Yggdrasil* classes.
        SyncYggdrasilClient client = new SyncYggdrasilClient(USERNAME, PASSWORD);

        System.out.println(pair);
        client.setTokenPair(pair);

        client.refresh();
        System.out.println(client.getTokenPair());
    }
}
