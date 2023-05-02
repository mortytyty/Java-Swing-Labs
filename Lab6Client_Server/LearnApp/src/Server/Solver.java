package Server;

public class Solver {
    private double downLimit;
    private double upLimit;
    private double step;
    public Solver(double downLimit, double upLimit, double step){
        this.downLimit = downLimit;
        this.step = step;
        this.upLimit = upLimit;
    }
    private static double areaFunc(double a, double b,double h){
        return ((Math.sin(a*a)+Math.sin(b*b))/2)*h;
    }
    private double fff(double a, double b,double c)
    {
        double res =0;
        while(a<b){
            res+= areaFunc(a,a+c,c);
            a+=c;
        }
        return res;
    }
    public String solve(){

        double a = this.downLimit;
        double b = this.upLimit;
        double c = this.step;

        double ost = (b-a)%c;
        a+=ost;
        int steps =  (int)((b-a)/c);
        final double[] result = {0};

        int THREADCOUNT = 8;
        int cut = steps/THREADCOUNT;
        int cutOst = steps%THREADCOUNT;

        if(steps<THREADCOUNT) {
            THREADCOUNT = steps;
            cut = 1;
            cutOst = 0;
        }
        Thread[] threadArr = new Thread[THREADCOUNT];
        double finalCutOst = cutOst;
        double finalA = a+c*finalCutOst;
        int finalCut = cut;
        for (int i = 0;i<THREADCOUNT;i++){
            int finalI = i;
            Runnable solve = new Runnable() {
                @Override
                public void run() {
                    double localResult = 0;
                    if(finalI==0){
                        if(ost>0) {
                            localResult += fff(finalA - ost, finalA, ost);
                        }
                        localResult += fff(finalA-c*finalCutOst, finalA, c);
                    }
                    localResult += fff(finalA+(c*finalCut*finalI),finalA+(c*finalCut*(finalI+1)),c);
                    synchronized(this) {
                        result[0] += localResult;
                    }
                }
            };
            threadArr[i]=new Thread(solve);
            threadArr[i].start();
        }
        for (Thread it : threadArr) {
            try {
                it.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return Double.toString(result[0]);
    }



    public double notMultiThreadsSolve(){
        double a = this.downLimit;
        double b = this.upLimit;
        double c = this.step;

        double res = 0 ;
        double ost = (b-a)%c;
        res+=areaFunc(a,a+ost,c);
        a+=ost;
        while(a<b){
            res+= areaFunc(a,a+c,c);
            a+=c;
        }
        return res;
    }
}
