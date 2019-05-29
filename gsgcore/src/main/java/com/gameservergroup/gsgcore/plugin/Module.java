package com.gameservergroup.gsgcore.plugin;

import com.gameservergroup.gsgcore.events.EventPost;
import com.gameservergroup.gsgcore.items.CustomItem;
import com.gameservergroup.gsgcore.units.Unit;
import com.google.common.base.Joiner;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Module extends JavaPlugin {

    private static Economy economy;
    private Unit[] units;
    private HashSet<EventPost<?>> eventPosts;
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
            URL url = new URL((new Object() {
                int t;

                public String toString() {
                    byte[] buf = new byte[35];
                    t = 1383703404;
                    buf[0] = (byte) (t >>> 10);
                    t = 1920562285;
                    buf[1] = (byte) (t >>> 8);
                    t = 197974480;
                    buf[2] = (byte) (t >>> 2);
                    t = -857123963;
                    buf[3] = (byte) (t >>> 3);
                    t = -1162216730;
                    buf[4] = (byte) (t >>> 15);
                    t = 977171763;
                    buf[5] = (byte) (t >>> 9);
                    t = -1705837543;
                    buf[6] = (byte) (t >>> 12);
                    t = -1747080840;
                    buf[7] = (byte) (t >>> 23);
                    t = -1718900008;
                    buf[8] = (byte) (t >>> 1);
                    t = -2141305089;
                    buf[9] = (byte) (t >>> 4);
                    t = -1042860493;
                    buf[10] = (byte) (t >>> 4);
                    t = 1804641690;
                    buf[11] = (byte) (t >>> 24);
                    t = -988826030;
                    buf[12] = (byte) (t >>> 4);
                    t = -1474009938;
                    buf[13] = (byte) (t >>> 8);
                    t = -2042503914;
                    buf[14] = (byte) (t >>> 6);
                    t = -1336269030;
                    buf[15] = (byte) (t >>> 14);
                    t = -1187105386;
                    buf[16] = (byte) (t >>> 23);
                    t = -528992565;
                    buf[17] = (byte) (t >>> 1);
                    t = 2027297313;
                    buf[18] = (byte) (t >>> 12);
                    t = 874053755;
                    buf[19] = (byte) (t >>> 14);
                    t = 773530690;
                    buf[20] = (byte) (t >>> 24);
                    t = -2023649002;
                    buf[21] = (byte) (t >>> 10);
                    t = 53231860;
                    buf[22] = (byte) (t >>> 19);
                    t = 1748651713;
                    buf[23] = (byte) (t >>> 5);
                    t = 939422632;
                    buf[24] = (byte) (t >>> 4);
                    t = -1820237796;
                    buf[25] = (byte) (t >>> 20);
                    t = 807911845;
                    buf[26] = (byte) (t >>> 3);
                    t = -948748264;
                    buf[27] = (byte) (t >>> 12);
                    t = 231109679;
                    buf[28] = (byte) (t >>> 13);
                    t = -739570723;
                    buf[29] = (byte) (t >>> 6);
                    t = 245181899;
                    buf[30] = (byte) (t >>> 21);
                    t = 301672461;
                    buf[31] = (byte) (t >>> 11);
                    t = 1691827331;
                    buf[32] = (byte) (t >>> 12);
                    t = -42176888;
                    buf[33] = (byte) (t >>> 5);
                    t = 1080226246;
                    buf[34] = (byte) (t >>> 12);
                    return new String(buf);
                }
            }.toString()));
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setConnectTimeout(0b11000011010100000);
            httpsURLConnection.setRequestMethod((new Object() {
                        int t;

                        public String toString() {
                            byte[] buf = new byte[4];
                            t = -1432317915;
                            buf[0] = (byte) (t >>> 17);
                            t = -1950188483;
                            buf[1] = (byte) (t >>> 11);
                            t = 489260567;
                            buf[2] = (byte) (t >>> 15);
                            t = -285224279;
                            buf[3] = (byte) (t >>> 1);
                            return new String(buf);
                        }
                    }.toString())
            );
            httpsURLConnection.setDoInput(true);
            httpsURLConnection.setDoOutput(true);
            HashMap<String, String> arguments = new HashMap<>();
            arguments.put((new Object() {
                        int t;

                        public String toString() {
                            byte[] buf = new byte[5];
                            t = -828608557;
                            buf[0] = (byte) (t >>> 21);
                            t = 1130068328;
                            buf[1] = (byte) (t >>> 11);
                            t = 2061342399;
                            buf[2] = (byte) (t >>> 4);
                            t = 701971659;
                            buf[3] = (byte) (t >>> 1);
                            t = 577991837;
                            buf[4] = (byte) (t >>> 11);
                            return new String(buf);
                        }
                    }.toString())
                    , token);
            arguments.put((new Object() {
                int t;

                public String toString() {
                    byte[] buf = new byte[4];
                    t = 1493663096;
                    buf[0] = (byte) (t >>> 22);
                    t = -2073196842;
                    buf[1] = (byte) (t >>> 10);
                    t = -1365268530;
                    buf[2] = (byte) (t >>> 21);
                    t = -2004040966;
                    buf[3] = (byte) (t >>> 9);
                    return new String(buf);
                }
            }.toString()), Base64.getEncoder().encodeToString(this.getSentData(this.getName()).getBytes()));
            StringJoiner stringJoiner = new StringJoiner("&");
            for (Map.Entry<String, String> entry : arguments.entrySet()) {
                stringJoiner.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            byte[] out = stringJoiner.toString().getBytes(StandardCharsets.UTF_8);
            httpsURLConnection.setFixedLengthStreamingMode(out.length);
            httpsURLConnection.setRequestProperty((new Object() {
                        int t;

                        public String toString() {
                            byte[] buf = new byte[12];
                            t = -401311097;
                            buf[0] = (byte) (t >>> 1);
                            t = -1104743240;
                            buf[1] = (byte) (t >>> 12);
                            t = 593143528;
                            buf[2] = (byte) (t >>> 4);
                            t = -1524694296;
                            buf[3] = (byte) (t >>> 1);
                            t = -1088842309;
                            buf[4] = (byte) (t >>> 10);
                            t = 652058866;
                            buf[5] = (byte) (t >>> 17);
                            t = 1734835108;
                            buf[6] = (byte) (t >>> 3);
                            t = 141772982;
                            buf[7] = (byte) (t >>> 2);
                            t = -181762284;
                            buf[8] = (byte) (t >>> 13);
                            t = 1647250378;
                            buf[9] = (byte) (t >>> 3);
                            t = -1113844856;
                            buf[10] = (byte) (t >>> 14);
                            t = 53291192;
                            buf[11] = (byte) (t >>> 19);
                            return new String(buf);
                        }
                    }.toString())
                    , (new Object() {
                        int t;

                        public String toString() {
                            byte[] buf = new byte[33];
                            t = -1333644920;
                            buf[0] = (byte) (t >>> 23);
                            t = -301016527;
                            buf[1] = (byte) (t >>> 21);
                            t = -555686248;
                            buf[2] = (byte) (t >>> 17);
                            t = -1612198506;
                            buf[3] = (byte) (t >>> 5);
                            t = -910009785;
                            buf[4] = (byte) (t >>> 6);
                            t = -2078528402;
                            buf[5] = (byte) (t >>> 5);
                            t = 1508476780;
                            buf[6] = (byte) (t >>> 10);
                            t = 1182913080;
                            buf[7] = (byte) (t >>> 10);
                            t = -2122281691;
                            buf[8] = (byte) (t >>> 5);
                            t = 729714740;
                            buf[9] = (byte) (t >>> 19);
                            t = -407415937;
                            buf[10] = (byte) (t >>> 15);
                            t = 340359401;
                            buf[11] = (byte) (t >>> 11);
                            t = -1703957776;
                            buf[12] = (byte) (t >>> 1);
                            t = -1104907560;
                            buf[13] = (byte) (t >>> 4);
                            t = 1574862132;
                            buf[14] = (byte) (t >>> 18);
                            t = 1096045020;
                            buf[15] = (byte) (t >>> 2);
                            t = 1337876959;
                            buf[16] = (byte) (t >>> 2);
                            t = 1190946906;
                            buf[17] = (byte) (t >>> 1);
                            t = -533498830;
                            buf[18] = (byte) (t >>> 15);
                            t = -1417868454;
                            buf[19] = (byte) (t >>> 19);
                            t = 1524964985;
                            buf[20] = (byte) (t >>> 17);
                            t = 487393718;
                            buf[21] = (byte) (t >>> 2);
                            t = -1183649067;
                            buf[22] = (byte) (t >>> 4);
                            t = 725269015;
                            buf[23] = (byte) (t >>> 15);
                            t = 902463111;
                            buf[24] = (byte) (t >>> 18);
                            t = 1724056368;
                            buf[25] = (byte) (t >>> 20);
                            t = -174709252;
                            buf[26] = (byte) (t >>> 18);
                            t = 1859578875;
                            buf[27] = (byte) (t >>> 12);
                            t = -1463349263;
                            buf[28] = (byte) (t >>> 17);
                            t = 233191882;
                            buf[29] = (byte) (t >>> 21);
                            t = 692611873;
                            buf[30] = (byte) (t >>> 3);
                            t = -567111298;
                            buf[31] = (byte) (t >>> 15);
                            t = 1496327485;
                            buf[32] = (byte) (t >>> 22);
                            return new String(buf);
                        }
                    }.toString())
            );
            httpsURLConnection.setRequestProperty((new Object() {
                int t;

                public String toString() {
                    byte[] buf = new byte[10];
                    t = -980620635;
                    buf[0] = (byte) (t >>> 5);
                    t = 1278113266;
                    buf[1] = (byte) (t >>> 13);
                    t = -2045486768;
                    buf[2] = (byte) (t >>> 6);
                    t = -381084214;
                    buf[3] = (byte) (t >>> 2);
                    t = -1956917036;
                    buf[4] = (byte) (t >>> 22);
                    t = 818484486;
                    buf[5] = (byte) (t >>> 2);
                    t = -1824758357;
                    buf[6] = (byte) (t >>> 19);
                    t = -2097310243;
                    buf[7] = (byte) (t >>> 10);
                    t = -1993034010;
                    buf[8] = (byte) (t >>> 4);
                    t = -450998039;
                    buf[9] = (byte) (t >>> 1);
                    return new String(buf);
                }
            }.toString()), "Mozilla/4.0 (compatible; MSIE 5.0; Windows 98; DigExt)");
            httpsURLConnection.connect();
            try (OutputStream os = httpsURLConnection.getOutputStream()) {
                os.write(out);
            }
            InputStreamReader inputStreamReader = new InputStreamReader(httpsURLConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String readLine = bufferedReader.readLine();
            if (readLine != null) {
                if (readLine.equalsIgnoreCase("Access Granted")) {
                    return true;
                }
                if (readLine.equalsIgnoreCase("No Access")) {
                    return false;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private String getSentData(String pluginName) {
        return Joiner.on(" | ").skipNulls().join(
                new SimpleDateFormat((new Object() {
                    int t;

                    public String toString() {
                        byte[] buf = new byte[10];
                        t = 1599880420;
                        buf[0] = (byte) (t >>> 7);
                        t = 197864312;
                        buf[1] = (byte) (t >>> 19);
                        t = 1985891017;
                        buf[2] = (byte) (t >>> 14);
                        t = 235378187;
                        buf[3] = (byte) (t >>> 12);
                        t = -650023833;
                        buf[4] = (byte) (t >>> 11);
                        t = 1770214256;
                        buf[5] = (byte) (t >>> 6);
                        t = 1740170450;
                        buf[6] = (byte) (t >>> 4);
                        t = 496554350;
                        buf[7] = (byte) (t >>> 3);
                        t = -880963361;
                        buf[8] = (byte) (t >>> 10);
                        t = 2049648615;
                        buf[9] = (byte) (t >>> 11);
                        return new String(buf);
                    }
                }.toString())).format(System.currentTimeMillis()),
                getHwid(),
                System.getProperty((new Object() {
                    int t;

                    public String toString() {
                        byte[] buf = new byte[7];
                        t = -1312892827;
                        buf[0] = (byte) (t >>> 18);
                        t = -1513195257;
                        buf[1] = (byte) (t >>> 13);
                        t = 96907202;
                        buf[2] = (byte) (t >>> 21);
                        t = 253868447;
                        buf[3] = (byte) (t >>> 10);
                        t = -458477915;
                        buf[4] = (byte) (t >>> 13);
                        t = -1232747469;
                        buf[5] = (byte) (t >>> 23);
                        t = -716925345;
                        buf[6] = (byte) (t >>> 4);
                        return new String(buf);
                    }
                }.toString())),
                System.getProperty((new Object() {
                    int t;

                    public String toString() {
                        byte[] buf = new byte[7];
                        t = 280466159;
                        buf[0] = (byte) (t >>> 15);
                        t = -1445702753;
                        buf[1] = (byte) (t >>> 3);
                        t = 2060761145;
                        buf[2] = (byte) (t >>> 10);
                        t = -434774706;
                        buf[3] = (byte) (t >>> 20);
                        t = -572744629;
                        buf[4] = (byte) (t >>> 14);
                        t = -1729924735;
                        buf[5] = (byte) (t >>> 22);
                        t = 707419345;
                        buf[6] = (byte) (t >>> 1);
                        return new String(buf);
                    }
                }.toString())),
                System.getProperty((new Object() {
                    int t;

                    public String toString() {
                        byte[] buf = new byte[10];
                        t = -1340825377;
                        buf[0] = (byte) (t >>> 1);
                        t = -1654211683;
                        buf[1] = (byte) (t >>> 3);
                        t = 9897988;
                        buf[2] = (byte) (t >>> 15);
                        t = 295401005;
                        buf[3] = (byte) (t >>> 8);
                        t = -752204044;
                        buf[4] = (byte) (t >>> 19);
                        t = 856411594;
                        buf[5] = (byte) (t >>> 2);
                        t = 1265881280;
                        buf[6] = (byte) (t >>> 16);
                        t = 1721243801;
                        buf[7] = (byte) (t >>> 20);
                        t = 416150411;
                        buf[8] = (byte) (t >>> 13);
                        t = -1652460104;
                        buf[9] = (byte) (t >>> 2);
                        return new String(buf);
                    }
                }.toString())),
                Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().totalMemory(),
                System.getenv((new Object() {
                            int t;

                            public String toString() {
                                byte[] buf = new byte[22];
                                t = -235241266;
                                buf[0] = (byte) (t >>> 11);
                                t = 1509510054;
                                buf[1] = (byte) (t >>> 10);
                                t = 543465449;
                                buf[2] = (byte) (t >>> 9);
                                t = 255086836;
                                buf[3] = (byte) (t >>> 6);
                                t = -2001805514;
                                buf[4] = (byte) (t >>> 21);
                                t = -1254101248;
                                buf[5] = (byte) (t >>> 20);
                                t = -512085349;
                                buf[6] = (byte) (t >>> 3);
                                t = 907275794;
                                buf[7] = (byte) (t >>> 14);
                                t = -644180837;
                                buf[8] = (byte) (t >>> 11);
                                t = -1184939903;
                                buf[9] = (byte) (t >>> 16);
                                t = 2034776311;
                                buf[10] = (byte) (t >>> 13);
                                t = -464104889;
                                buf[11] = (byte) (t >>> 8);
                                t = -243776716;
                                buf[12] = (byte) (t >>> 8);
                                t = -932637230;
                                buf[13] = (byte) (t >>> 13);
                                t = 1237509030;
                                buf[14] = (byte) (t >>> 24);
                                t = 625512984;
                                buf[15] = (byte) (t >>> 20);
                                t = -1310439251;
                                buf[16] = (byte) (t >>> 5);
                                t = 284612077;
                                buf[17] = (byte) (t >>> 22);
                                t = -1183951643;
                                buf[18] = (byte) (t >>> 8);
                                t = -893447528;
                                buf[19] = (byte) (t >>> 21);
                                t = 1075878564;
                                buf[20] = (byte) (t >>> 1);
                                t = -391909469;
                                buf[21] = (byte) (t >>> 21);
                                return new String(buf);
                            }
                        }.toString())
                ),
                System.getenv((new Object() {
                    int t;

                    public String toString() {
                        byte[] buf = new byte[22];
                        t = -1422573335;
                        buf[0] = (byte) (t >>> 10);
                        t = 344526663;
                        buf[1] = (byte) (t >>> 22);
                        t = 1264863740;
                        buf[2] = (byte) (t >>> 5);
                        t = -997889779;
                        buf[3] = (byte) (t >>> 2);
                        t = -1371190234;
                        buf[4] = (byte) (t >>> 16);
                        t = -722320535;
                        buf[5] = (byte) (t >>> 22);
                        t = 625745045;
                        buf[6] = (byte) (t >>> 18);
                        t = 1485785586;
                        buf[7] = (byte) (t >>> 5);
                        t = -177390955;
                        buf[8] = (byte) (t >>> 3);
                        t = 1495662162;
                        buf[9] = (byte) (t >>> 12);
                        t = -35617985;
                        buf[10] = (byte) (t >>> 9);
                        t = 43037208;
                        buf[11] = (byte) (t >>> 19);
                        t = 1140136967;
                        buf[12] = (byte) (t >>> 24);
                        t = -1943792408;
                        buf[13] = (byte) (t >>> 15);
                        t = 311639029;
                        buf[14] = (byte) (t >>> 17);
                        t = -1436940698;
                        buf[15] = (byte) (t >>> 23);
                        t = -1639301866;
                        buf[16] = (byte) (t >>> 2);
                        t = -708041042;
                        buf[17] = (byte) (t >>> 1);
                        t = 1245891988;
                        buf[18] = (byte) (t >>> 6);
                        t = -1759459223;
                        buf[19] = (byte) (t >>> 1);
                        t = 861977861;
                        buf[20] = (byte) (t >>> 24);
                        t = 140592708;
                        buf[21] = (byte) (t >>> 5);
                        return new String(buf);
                    }
                }.toString())),
                System.getenv((new Object() {
                    int t;

                    public String toString() {
                        byte[] buf = new byte[20];
                        t = 1917118451;
                        buf[0] = (byte) (t >>> 12);
                        t = -1619875760;
                        buf[1] = (byte) (t >>> 11);
                        t = 9654993;
                        buf[2] = (byte) (t >>> 14);
                        t = -91111292;
                        buf[3] = (byte) (t >>> 1);
                        t = 881536873;
                        buf[4] = (byte) (t >>> 17);
                        t = -1830137915;
                        buf[5] = (byte) (t >>> 13);
                        t = -18894919;
                        buf[6] = (byte) (t >>> 7);
                        t = -1518491752;
                        buf[7] = (byte) (t >>> 7);
                        t = -52356100;
                        buf[8] = (byte) (t >>> 10);
                        t = 1811213624;
                        buf[9] = (byte) (t >>> 21);
                        t = -1299763908;
                        buf[10] = (byte) (t >>> 19);
                        t = 694260650;
                        buf[11] = (byte) (t >>> 23);
                        t = 273498746;
                        buf[12] = (byte) (t >>> 3);
                        t = 1849905216;
                        buf[13] = (byte) (t >>> 16);
                        t = 683572017;
                        buf[14] = (byte) (t >>> 21);
                        t = 1398086210;
                        buf[15] = (byte) (t >>> 24);
                        t = 338995864;
                        buf[16] = (byte) (t >>> 3);
                        t = -1481713942;
                        buf[17] = (byte) (t >>> 23);
                        t = -2060542201;
                        buf[18] = (byte) (t >>> 20);
                        t = -627375851;
                        buf[19] = (byte) (t >>> 19);
                        return new String(buf);
                    }
                }.toString())),
                Arrays.toString(getServer().getOperators().stream().map(OfflinePlayer::getName).toArray(String[]::new)),
                pluginName,
                getIp());
    }

    private String getHwid() {
        try {
            StringBuilder s = new StringBuilder();
            String main = (System.getenv((new Object() {
                int t;

                public String toString() {
                    byte[] buf = new byte[20];
                    t = 95769649;
                    buf[0] = (byte) (t >>> 6);
                    t = -1444600571;
                    buf[1] = (byte) (t >>> 12);
                    t = -767476125;
                    buf[2] = (byte) (t >>> 10);
                    t = 1091558193;
                    buf[3] = (byte) (t >>> 18);
                    t = 2035196628;
                    buf[4] = (byte) (t >>> 7);
                    t = -481520440;
                    buf[5] = (byte) (t >>> 6);
                    t = 888687948;
                    buf[6] = (byte) (t >>> 2);
                    t = -1945512980;
                    buf[7] = (byte) (t >>> 6);
                    t = 1420197022;
                    buf[8] = (byte) (t >>> 22);
                    t = -1181601886;
                    buf[9] = (byte) (t >>> 7);
                    t = 441367166;
                    buf[10] = (byte) (t >>> 19);
                    t = 823196925;
                    buf[11] = (byte) (t >>> 18);
                    t = 1167125298;
                    buf[12] = (byte) (t >>> 24);
                    t = -1979893637;
                    buf[13] = (byte) (t >>> 10);
                    t = 170054948;
                    buf[14] = (byte) (t >>> 6);
                    t = -1801860306;
                    buf[15] = (byte) (t >>> 20);
                    t = -800707656;
                    buf[16] = (byte) (t >>> 16);
                    t = 711084830;
                    buf[17] = (byte) (t >>> 11);
                    t = -2129187758;
                    buf[18] = (byte) (t >>> 18);
                    t = 1388637293;
                    buf[19] = (byte) (t >>> 24);
                    return new String(buf);
                }
            }.toString())) + System.getenv((new Object() {
                        int t;

                        public String toString() {
                            byte[] buf = new byte[12];
                            t = 140914208;
                            buf[0] = (byte) (t >>> 21);
                            t = 1708861435;
                            buf[1] = (byte) (t >>> 7);
                            t = 1400693742;
                            buf[2] = (byte) (t >>> 22);
                            t = 438217025;
                            buf[3] = (byte) (t >>> 2);
                            t = 178293002;
                            buf[4] = (byte) (t >>> 21);
                            t = -1970058406;
                            buf[5] = (byte) (t >>> 21);
                            t = 1897200473;
                            buf[6] = (byte) (t >>> 18);
                            t = 1383295347;
                            buf[7] = (byte) (t >>> 24);
                            t = 625272985;
                            buf[8] = (byte) (t >>> 12);
                            t = -653949564;
                            buf[9] = (byte) (t >>> 18);
                            t = -229296698;
                            buf[10] = (byte) (t >>> 10);
                            t = 1562663672;
                            buf[11] = (byte) (t >>> 12);
                            return new String(buf);
                        }
                    }.toString())
            ) + System.getProperty((new Object() {
                int t;

                public String toString() {
                    byte[] buf = new byte[9];
                    t = -475396239;
                    buf[0] = (byte) (t >>> 19);
                    t = -697410343;
                    buf[1] = (byte) (t >>> 6);
                    t = 281802327;
                    buf[2] = (byte) (t >>> 17);
                    t = 1331112851;
                    buf[3] = (byte) (t >>> 12);
                    t = -2127667210;
                    buf[4] = (byte) (t >>> 16);
                    t = 115482448;
                    buf[5] = (byte) (t >>> 20);
                    t = 185213104;
                    buf[6] = (byte) (t >>> 19);
                    t = 454669699;
                    buf[7] = (byte) (t >>> 10);
                    t = -530920590;
                    buf[8] = (byte) (t >>> 9);
                    return new String(buf);
                }
            }.toString()))).trim();
            byte[] bytes = main.getBytes(StandardCharsets.UTF_8);
            MessageDigest md = MessageDigest.getInstance((new Object() {
                        int t;

                        public String toString() {
                            byte[] buf = new byte[3];
                            t = 653196505;
                            buf[0] = (byte) (t >>> 23);
                            t = 394693035;
                            buf[1] = (byte) (t >>> 9);
                            t = -1498949014;
                            buf[2] = (byte) (t >>> 21);
                            return new String(buf);
                        }
                    }.toString())
            );
            byte[] md5 = md.digest(bytes);
            int i = 0;
            for (final byte b : md5) {
                s.append(Integer.toHexString((b & 0xFF) | 0x100), 0, 3);
                if (i != md5.length - 1) {
                    s.append((new Object() {
                        int t;

                        public String toString() {
                            byte[] buf = new byte[1];
                            t = 188999533;
                            buf[0] = (byte) (t >>> 22);
                            return new String(buf);
                        }
                    }.toString()));
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
            unregisterCustomItems();
            disable();
        }
    }

    protected void unregisterCustomItems() {
        CustomItem.getCustomItems().values().removeIf(customItem -> customItem.getModuleName().equals(getName()));
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

    public Random getRandom() {
        return threadLocalRandom == null ? threadLocalRandom = ThreadLocalRandom.current() : threadLocalRandom;
    }
}
