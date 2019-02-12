package tai.space.datagenerator.demo;

public class GeneratedObject implements Event {
    private int myID;
    private String myDate;
    private Double myTemperature;
    private String myIndex;
    private String myType;

    public GeneratedObject(int myID, String myDate, Double myTemperature,String myIndex,String myType)
    {
        this.myID = myID;
        this.myDate = myDate;
        this.myTemperature = myTemperature;
        this.myIndex = myIndex;
        this.myType = myType;
    }

    public GeneratedObject(String myDate, Double myTemperature)
    {
        this.myDate = myDate;
        this.myTemperature = myTemperature;
        this.myType="generatedtemperature";
        if(myTemperature < 5 || myTemperature > 25){
            this.myIndex = "myoutlierindex";
        }
        else {
            this.myIndex = "mynormalindex";
        }
    }

    public String getMyType() {
        return myType;
    }

    public String getMyIndex() {
        return myIndex;
    }

    public Double getMyTemperature() {
        return myTemperature;
    }

    public String getMyDate() { return myDate; }

    public int getMyID() {
        return myID;
    }

}
