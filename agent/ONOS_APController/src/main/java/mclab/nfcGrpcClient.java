package mclab;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.internal.DnsNameResolverProvider;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.okhttp.OkHttpChannelBuilder;
import io.netty.channel.*;
import io.grpc.Channel;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by mclab on 17. 3. 29.
 */

public class nfcGrpcClient {

    private Logger logger = Logger.getLogger(nfcGrpcClient.class.getName());
    private final ManagedChannel channel;
    private final DeviceConfigGrpc.DeviceConfigBlockingStub blockingStub;
    private boolean isExist;

    private String IP;
    private String SSID;
    private String Channel;
    private String HW_mode;
    private String on;



    nfcGrpcClient(String host, int port) {
        this(OkHttpChannelBuilder.forAddress(host, port)
                //ttt
                .nameResolverFactory(new DnsNameResolverProvider())
                .usePlaintext(true));
    }

    nfcGrpcClient(String host, int port, String to_check_milestone) {
        channel = OkHttpChannelBuilder.forAddress(host, port).nameResolverFactory(new DnsNameResolverProvider())
                 .usePlaintext(true).build();
        blockingStub = DeviceConfigGrpc.newBlockingStub(channel);
        this.IP = host;
    }


    nfcGrpcClient(OkHttpChannelBuilder channelBuilder) {
        channel = channelBuilder.build();
        blockingStub = DeviceConfigGrpc.newBlockingStub(channel);
        isExist = this.Hello();
        channel.shutdown();

    }


    nfcGrpcClient(NettyChannelBuilder channelBuilder) {
        channel = channelBuilder.build();
        blockingStub = DeviceConfigGrpc.newBlockingStub(channel);
        isExist = this.Hello();
        channel.shutdown();


    }

    private void update(HelloResponse response) {

        IP = response.getIp();
        SSID = response.getSsid();
        Channel = response.getChannel();
        HW_mode=response.getHwMode();
        on = response.getPowerOnOff();

    }
    /*
    private void update(EditConfigResponse response) {

        IP = response.getIp();
        SSID = response.getSsid();
        Channel = response.getChannel();
        HW_mode=response.getHwMode();
        on = response.getPowerOnOff();

    }
    private void update(GetConfigResponse response) {

        IP = response.getIp();
        SSID = response.getSsid();
        Channel = response.getChannel();
        HW_mode=response.getHwMode();
        on = response.getPowerOnOff();

    }*/

    @Override
    public String toString() {
        StringBuilder strbuild = new StringBuilder();
        strbuild.append("{");
        strbuild.append("\n   IP        : " + this.IP);
        strbuild.append("\n   SSID      : " + this.SSID);
        strbuild.append("\n   channel   : " + this.Channel);
        strbuild.append("\n   HW_mode   : " + this.HW_mode);
        strbuild.append("\n}\n\n");

        return strbuild.toString();
    }

    public boolean checkExist() { return isExist; }



    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }



    public boolean Hello() {
        HelloRequest request = HelloRequest.newBuilder().setHello("hello").build();
        HelloResponse response;

        try {
            response = blockingStub.hello(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return false;
        }
        update(response);
        return true;
    }




    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getChannel() {
        return Channel;
    }

    public void setChannel(String channel) {
        Channel = channel;
    }

    public String getHW_mode() {
        return HW_mode;
    }

    public void setHW_mode(String HW_mode) {
        this.HW_mode = HW_mode;
    }

    public String getOn() {
        return on;
    }

    public void setOn(String on) {
        this.on = on;
    }




}
