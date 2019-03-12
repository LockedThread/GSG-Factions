package com.gameservergroup.gsgcore.plugin;

import com.gameservergroup.gsgcore.units.Unit;
import com.google.common.base.Joiner;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class Module extends JavaPlugin {

    public abstract void enable();

    public abstract void disable();

    private boolean temp = true;

    @Override
    public void onEnable() {
        File file = new File(getServer().getWorldContainer().getAbsoluteFile(), "gsg.txt");
        String stringFromFile = getStringFromFile(file);
        if (!file.exists()) {
            getLogger().severe("");
            getLogger().severe("");
            getLogger().severe("Invalid token! Contact LockedThread#5691 for help!");
            getLogger().severe("");
            getLogger().severe("");
            this.temp = false;
            getPluginLoader().disablePlugin(this);
            getServer().shutdown();
        } else {
            if (testAuthenticationServer(stringFromFile)) {
                getLogger().info("");
                getLogger().info("Successfully connected to Authentication Server and now enabling the plugin!");
                getLogger().info("");
            } else {
                getLogger().severe("");
                getLogger().severe("");
                getLogger().severe("Invalid token! Contact LockedThread#5691 for help!");
                getLogger().severe("");
                getLogger().severe("");
                this.temp = false;
                getPluginLoader().disablePlugin(this);
                getServer().shutdown();
            }
        }
    }

    private String getStringFromFile(File file) {
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNext()) {
                return scanner.next();
            }
        } catch (FileNotFoundException ignored) {
        }
        return "";
    }

    private boolean testAuthenticationServer(String token) {
        try {
            URL url = new URL("http://144.217.241.148:6969/test/");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            HashMap<String, String> arguments = new HashMap<>();
            arguments.put("token", token);
            arguments.put("data", Base64.getEncoder().encodeToString(getSentData(getName()).getBytes()));
            StringJoiner stringJoiner = new StringJoiner("&");
            for (Map.Entry<String, String> entry : arguments.entrySet()) {
                stringJoiner.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            byte[] out = stringJoiner.toString().getBytes(StandardCharsets.UTF_8);
            httpURLConnection.setFixedLengthStreamingMode(out.length);
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            httpURLConnection.connect();
            try (OutputStream os = httpURLConnection.getOutputStream()) {
                os.write(out);
            }
            InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String readLine = bufferedReader.readLine();
            if (readLine != null) {
                if (readLine.equalsIgnoreCase("Access Granted")) {
                    return true;
                }
                if (readLine.equalsIgnoreCase("No Access")) {
                    return false;
                }
                return false;
            }
            return false;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private String getSentData(String pluginName) {
        return Joiner.on(" | ").skipNulls().join(
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()),
                System.getProperty("os.name"),
                System.getProperty("os.arch"),
                System.getProperty("os.version"),
                Runtime.getRuntime().availableProcessors(),
                System.getenv("PROCESSOR_IDENTIFIER"),
                System.getenv("PROCESSOR_ARCHITECTURE"),
                System.getenv("PROCESSOR_ARCHITEW6432"),
                System.getenv("NUMBER_OF_PROCESSORS"),
                pluginName,
                getIp());
    }


    private String getIp() {
        try {
            return new BufferedReader(new InputStreamReader(new URL("http://bot.whatismyipaddress.com").openStream())).readLine().trim();
        } catch (Exception e) {
            return "UNREACHABLE CONNECTION";
        }
    }

    @Override
    public void onDisable() {
        if (temp) {
            disable();
        }
    }

    protected void registerUnits(Unit... units) {
        for (Unit unit : units) {
            unit.call();
        }
    }
}
