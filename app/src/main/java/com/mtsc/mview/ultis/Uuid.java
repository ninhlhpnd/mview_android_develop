package com.mtsc.mview.ultis;

import com.mtsc.mview.R;
import com.mtsc.mview.model.CamBien;

import java.util.ArrayList;
import java.util.List;

public class Uuid {
    public static String readUuidHm10="00004320-0000-1000-8000-00805f9b34fb";
    public static String serviceUuidHm10="00004321-0000-1000-8000-00805f9b34fb";
//    public static String writeUuidEsp="4a5c0000-0002-0000-0000-5c1e741f1c00";
    public static String serviceUuidEsp="4a5c0000-0000-0000-0000-5c1e741f1c00";
    public static String readUuidEsp="4a5c0000-0003-0000-0000-5c1e741f1c00";


    public static String nameFilter="Temp,Humid,Pressure,Oxygen,CO2,SoundI,PH,Salinity,DissolveOxy,Voltage,Current," +
            "Force,SoundF,Distance,Conductivity,V&A,P&T,H&T,Light";
//    public static String nameFilter="Force,Distance";

    public static String Temp="Nhiệt độ";
    public static String[] dvTemp={"°C","°F","°K"};
    public static double[][] hesoTemp={{1,0},{1.8,32},{1,273}};
    public static int[] tansoTemp={1,2,5,10};
    public static double[] daiDoTemp={-20,130};

    public static String Humid="Độ ẩm";
    public static String[] dvHumid={"%"};
    public static double[][] hesoHumid={{1,0}};
    public static int[] tansoHumid={1,2,5,10};
    public static double[] daiDoHumid={0,100};

    public static String Pressure="Áp suất";
    public static String[] dvPressure={"kPa","bar","mmHg"};
    public static double[][] hesoPressure={{1,0},{0.01,0},{7.5,0}};
    public static int[] tansoPressure={1,2,5,10};
    public static double[] daiDoPressure={0,300};

    public static String Oxygen="Nồng độ Oxy";
    public static String[] dvOxygen={"%"};
    public static double[][] hesoOxygen={{1,0}};
    public static int[] tansoOxygen={1,2,5,10};
    public static double[] daiDoOxygen={0,30};

    public static String CO2="Nồng độ CO2";
    public static String[] dvCO2={"ppm","ppt"};
    public static double[][] hesoCO2={{1,0},{0.001,0}};
    public static int[] tansoCO2={1,2,5,10};
    public static double[] daiDoCO2={0,50000};

    public static String SoundI="Cường độ âm thanh";
    public static String[] dvSoundI={"dB"};
    public static double[][] hesoSoundI={{1,0}};
    public static int[] tansoSoundI={5,10,20,50};
    public static double[] daiDoSoundI={0,130};

    public static String PH="pH";
    public static String[] dvPH={""};
    public static double[][] hesoPH={{1,0}};
    public static int[] tansoPH={1,2,5,10};
    public static double[] daiDoPH={0,14};

    public static String Salinity="Nồng độ muối";
    public static String[] dvSalinity={"ppt"};
    public static double[][] hesoSalinity={{1,0}};
    public static int[] tansoSalinity={1,2,5,10};
    public static double[] daiDoSalinity={0,50};

    public static String DissolveOxy="Nồng độ oxy trong nước";
    public static String[] dvDissolveOxy={"mg/l"};
    public static double[][] hesoDissolveOxy={{1,0}};
    public static int[] tansoDissolveOxy={1,2,5,10};
    public static double[] daiDoDissolveOxy={0,20};

    public static String Voltage="Điện áp";
    public static String[] dvVoltage={"V","mV"};
    public static double[][] hesoVoltage={{1,0},{1000,0}};
    public static int[] tansoVoltage={1,2,5,10,20,50,100,200,500,1000};
    public static double[] daiDoVoltage={-14,14};

    public static String Current="Dòng điện";
    public static String[] dvCurrent={"A","mA"};
    public static double[][] hesoCurrent={{1,0},{1000,0}};
    public static int[] tansoCurrent={1,2,5,10,20,50,100,200,500,1000};
    public static double[] daiDoCurrent={-2,2};

    public static String Force="Lực";
    public static String[] dvForce={"N","mN"};
    public static double[][] hesoForce={{1,0},{1000,0}};
    public static int[] tansoForce={1,2,5,10};
    public static double[] daiDoForce={-50,50};

    public static String Frequency="Tần số";
    public static String[] dvFrequency={"Hz"};
    public static double[][] hesoFrequency={{1,0}};
    public static int[] tansoFrequency={1,2,5,10};
    public static double[] daiDoFrequency={20,20000};

    public static String Vitri="Vị trí";
    public static String[] dvVitri={"cm","mm","m"};
    public static double[][] hesoVitri={{1,0},{10,0},{0.01,0}};
    public static int[] tansoVitri={1,2,5,10,20,50};
    public static double[] daiDoVitri={0,400};

    public static String Dodan="Độ dẫn";
    public static String[] dvDodan={"uS","mS"};
    public static double[][] hesoDodan={{1,0},{0.001,0}};
    public static int[] tansoDodan={1,2,5,10};
    public static double[] daiDoDodan={0,20000};

    public static String Vantoc="Vận tốc";
    public static String Giatoc="Gia tốc";
    public static List<CamBien> camBiens=new ArrayList<>();

    public static String Dosang="Cường độ sáng";
    public static String[] dvDosang={"lux"};
    public static double[][] hesoDosang={{1,0}};
    public static int[] tansoDosang={1,2,5,10};
    public static double[] daiDoDosang={0,55000};
    public static int[] iconDevice={R.drawable.temp,R.drawable.humid,R.drawable.pressure,R.drawable.oxygen,
            R.drawable.co2,R.drawable.soundi,R.drawable.ph,R.drawable.salinity,
            R.drawable.dissolved,R.drawable.force,R.drawable.current,R.drawable.voltage,R.drawable.soundf,
            R.drawable.distance,R.drawable.conductivity,R.drawable.dongap,R.drawable.anhsang };


}
