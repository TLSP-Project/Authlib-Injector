// Decompiled with: CFR 0.151
// Class Version: 8
package com.mojang.authlib.yggdrasil;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.minecraft.HttpMinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.request.JoinMinecraftServerRequest;
import com.mojang.authlib.yggdrasil.response.HasJoinedMinecraftServerResponse;
import com.mojang.authlib.yggdrasil.response.MinecraftProfilePropertiesResponse;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.authlib.yggdrasil.response.Response;
import com.mojang.util.UUIDTypeAdapter;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YggdrasilMinecraftSessionService
        extends HttpMinecraftSessionService {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String BASE_URL = "http://127.0.0.1:14250/yggdrasil/77114517833647104/session/minecraft/";
    private static final URL JOIN_URL = HttpAuthenticationService.constantURL("http://127.0.0.1:14250/yggdrasil/77114517833647104/session/minecraft/join");
    private static final URL CHECK_URL = HttpAuthenticationService.constantURL("http://127.0.0.1:14250/yggdrasil/77114517833647104/session/minecraft/hasJoined");
    private final PublicKey publicKey;
    private final Gson gson = new GsonBuilder().registerTypeAdapter((Type)((Object)UUID.class), new UUIDTypeAdapter()).create();
    private final LoadingCache<GameProfile, GameProfile> insecureProfiles = CacheBuilder.newBuilder().expireAfterWrite(6L, TimeUnit.HOURS).build(new CacheLoader<GameProfile, GameProfile>(){

        @Override
        public GameProfile load(GameProfile key) throws Exception {
            return YggdrasilMinecraftSessionService.this.fillGameProfile(key, false);
        }
    });

    protected YggdrasilMinecraftSessionService(YggdrasilAuthenticationService authenticationService) {
        super(authenticationService);
        try {
            X509EncodedKeySpec spec = new X509EncodedKeySpec(IOUtils.toByteArray(YggdrasilMinecraftSessionService.class.getResourceAsStream("/yggdrasil_session_pubkey.der")));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.publicKey = keyFactory.generatePublic(spec);
        }
        catch (Exception ignored) {
            throw new Error("Missing/invalid yggdrasil public key!");
        }
    }

    @Override
    public void joinServer(GameProfile profile, String authenticationToken, String serverId) throws AuthenticationException {
        JoinMinecraftServerRequest request = new JoinMinecraftServerRequest();
        request.accessToken = authenticationToken;
        request.selectedProfile = profile.getId();
        request.serverId = serverId;
        this.getAuthenticationService().makeRequest(JOIN_URL, request, Response.class);
    }

    @Override
    public GameProfile hasJoinedServer(GameProfile user, String serverId, InetAddress address) throws AuthenticationUnavailableException {
        HashMap<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("username", user.getName());
        arguments.put("serverId", serverId);
        if (address != null) {
            arguments.put("ip", address.getHostAddress());
        }
        URL url = HttpAuthenticationService.concatenateURL(CHECK_URL, HttpAuthenticationService.buildQuery(arguments));
        try {
            HasJoinedMinecraftServerResponse response = (HasJoinedMinecraftServerResponse)((Object)this.getAuthenticationService().makeRequest(url, null, HasJoinedMinecraftServerResponse.class));
            if (response != null && response.getId() != null) {
                GameProfile result = new GameProfile(response.getId(), user.getName());
                if (response.getProperties() != null) {
                    result.getProperties().putAll((Multimap)response.getProperties());
                }
                return result;
            }
            return null;
        }
        catch (AuthenticationUnavailableException e) {
            throw e;
        }
        catch (AuthenticationException ignored) {
            return null;
        }
    }

    @Override
    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(GameProfile profile, boolean requireSecure) {
        MinecraftTexturesPayload result;
        try {
            Property textureProperty = Iterables.getFirst(profile.getProperties().get("textures"), null);
            if (textureProperty == null) {
                //System.err.println("fillGameProfile " + profile.getId() + "\t " + profile.getName());
                textureProperty = Iterables.getFirst(this.fillGameProfile(profile, false).getProperties().get("textures"), null);
                if (textureProperty == null) {
                    //System.err.println("textureProperty is empty");
                    return new HashMap<MinecraftProfileTexture.Type, MinecraftProfileTexture>();
                }else{
                    //System.err.println("get base64Value " + textureProperty.getValue());
                }
            }
            String json = new String(Base64.decodeBase64(textureProperty.getValue()), Charsets.UTF_8);
            //System.err.println("Json is : " + json);
            if ((result = this.gson.fromJson(json, MinecraftTexturesPayload.class)) == null || result.getTextures() == null) {
                //System.err.println("Result is empty : " + json);
                return new HashMap<MinecraftProfileTexture.Type, MinecraftProfileTexture>();
            }
            //System.err.println("Result is : " + result.getTextures());
        }catch (Exception e){
            //System.err.println("获取材质时异常");
            e.printStackTrace();
            return new HashMap<MinecraftProfileTexture.Type, MinecraftProfileTexture>();
        }
        return result.getTextures();
    }

    @Override
    public GameProfile fillProfileProperties(GameProfile gameProfile, boolean bl) {
        return this.fillGameProfile(gameProfile, false);
    }

    protected GameProfile fillGameProfile(GameProfile profile, boolean requireSecure) {
        try {
            URL url = HttpAuthenticationService.constantURL("http://127.0.0.1:14250/yggdrasil/77114517833647104/session/minecraft/profile/" + UUIDTypeAdapter.fromUUID(profile.getId()));
            url = HttpAuthenticationService.concatenateURL(url, "unsigned=" + !requireSecure);
            MinecraftProfilePropertiesResponse response = this.getAuthenticationService().makeRequest(url, null, MinecraftProfilePropertiesResponse.class);
            if (response == null) {
                LOGGER.debug("Couldn't fetch profile properties for " + profile + " as the profile does not exist");
                return profile;
            }
            GameProfile result = new GameProfile(response.getId(), response.getName());
            result.getProperties().putAll(response.getProperties());
            profile.getProperties().putAll(response.getProperties());
            LOGGER.debug("Successfully fetched profile properties for " + profile);
            return result;
        }
        catch (AuthenticationException e) {
            LOGGER.warn("Couldn't look up profile properties for " + profile, e);
            return profile;
        }
    }

    @Override
    public YggdrasilAuthenticationService getAuthenticationService() {
        return (YggdrasilAuthenticationService)super.getAuthenticationService();
    }

    private static boolean isWhitelistedDomain(String string) {
        return true;
    }

}
