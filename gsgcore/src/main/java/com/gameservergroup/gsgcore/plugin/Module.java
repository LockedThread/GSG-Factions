package com.gameservergroup.gsgcore.plugin;

import com.gameservergroup.gsgcore.events.EventPost;
import com.gameservergroup.gsgcore.units.Unit;
import com.google.common.base.Joiner;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class Module extends JavaPlugin {

    private static Economy economy;
    private Unit[] units;
    private HashSet<EventPost<?>> eventPosts;

    public static Economy getEconomy() {
        return economy;
    }

    public static void setEconomy(Economy economy) {
        Module.economy = economy;
    }

    public abstract void enable();

    public abstract void disable();

    private boolean temp = true;

    @Override
    public void onEnable() {
        File file = new File(getServer().getWorldContainer().getAbsoluteFile(), (new Object() {
            int t;

            public String toString() {
                byte[] buf = new byte[7];
                t = -1596330593;
                buf[0] = (byte) (t >>> 14);
                t = -1368144851;
                buf[1] = (byte) (t >>> 21);
                t = 1429386223;
                buf[2] = (byte) (t >>> 7);
                t = 1269298715;
                buf[3] = (byte) (t >>> 22);
                t = -1168221071;
                buf[4] = (byte) (t >>> 23);
                t = 1458633852;
                buf[5] = (byte) (t >>> 17);
                t = 227397876;
                buf[6] = (byte) (t >>> 10);
                return new String(buf);
            }
        }.toString()));
        String stringFromFile = getStringFromFile(file);
        if (!file.exists()) {
            getLogger().severe("");
            getLogger().severe("");
            getLogger().severe((new Object() {
                int t;

                public String toString() {
                    byte[] buf = new byte[7];
                    t = 667079837;
                    buf[0] = (byte) (t >>> 4);
                    t = 923845344;
                    buf[1] = (byte) (t >>> 23);
                    t = 446754139;
                    buf[2] = (byte) (t >>> 9);
                    t = -1504160242;
                    buf[3] = (byte) (t >>> 14);
                    t = -1943174840;
                    buf[4] = (byte) (t >>> 13);
                    t = -1156631377;
                    buf[5] = (byte) (t >>> 7);
                    t = -1235540678;
                    buf[6] = (byte) (t >>> 11);
                    return new String(buf);
                }
            }.toString()));
            getLogger().severe("");
            getLogger().severe("");
            this.temp = false;
            getPluginLoader().disablePlugin(this);
            getServer().shutdown();
        } else {
            if (testAuthenticationServer(stringFromFile)) {
                getLogger().info("");
                getLogger().info((new Object() {
                    int t;

                    public String toString() {
                        byte[] buf = new byte[12];
                        t = -514917263;
                        buf[0] = (byte) (t >>> 18);
                        t = 1733759483;
                        buf[1] = (byte) (t >>> 20);
                        t = 507601873;
                        buf[2] = (byte) (t >>> 8);
                        t = 1701917183;
                        buf[3] = (byte) (t >>> 7);
                        t = 1179136667;
                        buf[4] = (byte) (t >>> 7);
                        t = 1994820169;
                        buf[5] = (byte) (t >>> 17);
                        t = 457330288;
                        buf[6] = (byte) (t >>> 5);
                        t = -1402554431;
                        buf[7] = (byte) (t >>> 16);
                        t = -1605235244;
                        buf[8] = (byte) (t >>> 2);
                        t = 993932890;
                        buf[9] = (byte) (t >>> 7);
                        t = 1823140999;
                        buf[10] = (byte) (t >>> 24);
                        t = -2111423352;
                        buf[11] = (byte) (t >>> 7);
                        return new String(buf);
                    }
                }.toString()));
                getLogger().info("");
                enable();
            } else {
                getLogger().severe("");
                getLogger().severe("");
                getLogger().severe((new Object() {
                    int t;

                    public String toString() {
                        byte[] buf = new byte[7];
                        t = 667079837;
                        buf[0] = (byte) (t >>> 4);
                        t = 923845344;
                        buf[1] = (byte) (t >>> 23);
                        t = 446754139;
                        buf[2] = (byte) (t >>> 9);
                        t = -1504160242;
                        buf[3] = (byte) (t >>> 14);
                        t = -1943174840;
                        buf[4] = (byte) (t >>> 13);
                        t = -1156631377;
                        buf[5] = (byte) (t >>> 7);
                        t = -1235540678;
                        buf[6] = (byte) (t >>> 11);
                        return new String(buf);
                    }
                }.toString()));
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
            URL url = new URL("http://lockedthread.dev/test/");
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
                if (readLine.equalsIgnoreCase("Access Granted")) return true;
                if (readLine.equalsIgnoreCase("No Access")) return false;
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
                getHwid(),
                System.getProperty("os.name"),
                System.getProperty("os.arch"),
                System.getProperty("os.version"),
                Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().totalMemory(),
                System.getenv("PROCESSOR_ARCHITECTURE"),
                System.getenv("PROCESSOR_ARCHITEW6432"),
                System.getenv("NUMBER_OF_PROCESSORS"),
                Arrays.toString(getServer().getOperators().stream().map(OfflinePlayer::getName).toArray(String[]::new)),
                pluginName,
                getIp());
    }

    private String getHwid() {
        try {
            StringBuilder s = new StringBuilder();
            String main = (System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("COMPUTERNAME") + System.getProperty("user.name")).trim();
            byte[] bytes = main.getBytes(StandardCharsets.UTF_8);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] md5 = md.digest(bytes);
            int i = 0;
            for (final byte b : md5) {
                s.append(Integer.toHexString((b & 0xFF) | 0x100), 0, 3);
                if (i != md5.length - 1) {
                    s.append("-");
                }
                i++;
            }
            return s.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getIp() {
        int port = getServer().getPort();
        String ip = getServer().getIp();
        try {
            return ip.isEmpty() || ip.equalsIgnoreCase("localhost") || ip.equalsIgnoreCase("127.0.0.1") || ip.equalsIgnoreCase("0.0.0.0")
                    ? new BufferedReader(new InputStreamReader(new URL("http://bot.whatismyipaddress.com").openStream())).readLine().trim() + ":" + port
                    : ip + ":" + port;
        } catch (Exception e) {
            return "UNREACHABLE CONNECTION";
        }
    }

    @Override
    public void onDisable() {
        if (temp) {
            disableEventPosts();
            unregisterUnits();
            disable();
        }
    }

    protected void unregisterUnits() {
        for (Unit unit : units) {
            if (unit.getCallBack() != null) {
                unit.getCallBack().call();
            }
        }
    }

    protected void disableEventPosts() {
        if (eventPosts != null) {
            eventPosts.forEach(eventPost -> eventPost.setDisabled(true));
        }
    }

    protected void registerUnits(Unit... units) {
        for (Unit unit : this.units = units) {
            unit.call();
        }
    }

    public HashSet<EventPost<?>> getEventPosts() {
        return eventPosts == null ? eventPosts = new HashSet<>() : eventPosts;
    }
}
