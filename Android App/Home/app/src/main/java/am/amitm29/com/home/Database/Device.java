package am.amitm29.com.home.Database;

public class Device {

    private String deviceName;
    private String deviceAddress;
    private  int deviceMajorClass;

    public Device(String deviceName, String deviceAddress, int deviceMajorClass) {
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
        this.deviceMajorClass = deviceMajorClass;
    }

    public int getDeviceMajorClass() {
        return deviceMajorClass;
    }

    public void setDeviceMajorClass(int deviceMajorClass) {
        this.deviceMajorClass = deviceMajorClass;
    }

    public void setDeviceName(String deviceName) {

        this.deviceName = deviceName;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public String getDeviceName() {

        return deviceName;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }
}
