package Client.UI;

import java.io.Serializable;

class RecordIntegral implements Serializable {
    private double upLimit;
    private double downLimit;
    private double step;
    private String result;

    public RecordIntegral(double downLimit,double upLimit,double step) throws SimpleException {
        if(upLimit<0.000001||upLimit>1000000||downLimit<0.000001||downLimit>1000000||step<0.000001||step>1000000) throw new SimpleException("Введены некорректные данные");
        this.upLimit=upLimit;
        this.downLimit=downLimit;
        this.step = step;
        result = "";
    }
    public double getUpLimit() {
        return upLimit;
    }
    public double getDownLimit() {
        return downLimit;
    }
    public double getStep() {
        return step;
    }
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
    public String toString(){
        return downLimit+" "+upLimit+" "+step+" "+result;
    }


}
