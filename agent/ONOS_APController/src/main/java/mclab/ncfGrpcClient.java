package mclab;

import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.internal.DnsNameResolverProvider;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.okhttp.OkHttpChannelBuilder;

import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;


/**
 * Created by mclab on 17. 3. 29.
 */

public class ncfGrpcClient {

    private Logger logger = Logger.getLogger(ncfGrpcClient.class.getName());
    private ManagedChannel channel;
    private DeviceConfigGrpc.DeviceConfigBlockingStub blockingStub;
    private boolean isExist;

    private String IP;
    private String SSID;
    private String Channel;
    private String HW_mode;
    private String on;
    private String password;
    public String error;



    ncfGrpcClient(String host, int port) {
        this(OkHttpChannelBuilder.forAddress(host, port)
                //ttt
                .nameResolverFactory(new DnsNameResolverProvider())
                .usePlaintext(true));
    }

    ncfGrpcClient(String host, int port, String to_check_milestone) {
        channel = OkHttpChannelBuilder.forAddress(host, port).nameResolverFactory(new DnsNameResolverProvider())
                 .usePlaintext(true).build();
        blockingStub = DeviceConfigGrpc.newBlockingStub(channel);
        this.IP = host;
    }


    ncfGrpcClient(OkHttpChannelBuilder channelBuilder) {
        channel = channelBuilder.build();
        blockingStub = DeviceConfigGrpc.newBlockingStub(channel);
        isExist = this.Hello();

    }


    ncfGrpcClient(NettyChannelBuilder channelBuilder) {
        channel = channelBuilder.build();
        blockingStub = DeviceConfigGrpc.newBlockingStub(channel);
        isExist = this.Hello();

    }

    private void update(HelloResponse response) {

        IP = response.getIp();
        SSID = response.getSsid();
        Channel = response.getChannel();
        HW_mode=response.getHwMode();
        on = response.getPowerOnOff();
        password = response.getPassword();

    }

    private void update(EditConfigResponse response) {

        IP = response.getIp();
        SSID = response.getSsid();
        Channel = response.getChannel();
        HW_mode=response.getHwMode();
        on = response.getPowerOnOff();
        password = response.getPassword();

    }

    private void update(GetConfigResponse response) {

        IP = response.getIp();
        SSID = response.getSsid();
        Channel = response.getChannel();
        HW_mode=response.getHwMode();
        on = response.getPowerOnOff();
        password = response.getPassword();

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


    public void shutdown() {
        channel.shutdown();
    }



    public boolean Hello() {
        HelloRequest request = HelloRequest.newBuilder().setHello("hello").build();
        HelloResponse response;

        try {
            response = blockingStub.hello(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            //this.error = e.getStatus().toString();
            return false;
        }
        update(response);
        return true;
    }

    public boolean EditConfig(String command, String value) {
        EditConfigRequest request = EditConfigRequest.newBuilder().setCommand(command).setValue(value).build();
        EditConfigResponse response;

        try {
            response = blockingStub.editConfig(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return false;
        }
        update(response);
        return true;
    }

    public boolean GetConfig() {
        GetConfigRequest request = GetConfigRequest.newBuilder().setCommand("get").build();
        GetConfigResponse response;

        try {
            response = blockingStub.getConfig(request);
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
