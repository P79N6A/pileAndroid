package com.swust.robotiumdemo;

import android.os.Environment;

import com.robotium.solo.Solo;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/12/21.
 */

public class ReportFile {

    private static int PEPORTFILEINDEX = 0;//测试步骤
    private static int passNum = 0; // 通过个数
    private static int errNum = 0; // 错误点个数

    /**
     * 创建单元测试文件并写入html表头
     *
     * @param caseName 测试用例名
     * @throws Exception
     */
    public static void createCaseReport(String caseName, String className) throws Exception {

        File sdCardDir = Environment.getExternalStorageDirectory();
        File path = new File(sdCardDir + "/" + className + ".html");// 创建文件
        if (!path.exists()) {
            path.createNewFile();
        }
        FileWriter fw = new FileWriter(path);
        fw.write("");// 清空数据
        fw.flush();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        String timeString = sdf.format(new Date());

        String caseContent = "<html>" + "<head>"
                + "<meta http-equiv='Content-Type' content='text/html  charset=UTF-8' />" + "<title>" + caseName
                + "</title>" + "</head>" +

                "<body>" + "<p align='right'>&nbsp;</p>"
                + "<p align='center'><font size='32' color='#0000C6' style='font-weight:bold;'>自动化测试报告</font> </p>"
                + "<p>&nbsp;</p>" + "<hr style='height:3px;border:none;border-top:3px solid  #0000C6;' />"
                + "<p align='right'>&nbsp;</p>" + "<p><font size='5'><strong>测试用例</strong>  :  " + caseName
                + "</font></p>" + "<p><font size='5'><strong>测试日期</strong>  :  " + timeString + "</font></p>"
                + "<p align='right'>&nbsp;</p>"
                + "<table width='0' border='1' style='width:100%' cellspacing='0' cellpadding='0'  height='100px;'>"
                + "<tr>" + "<td><div align='center'><font size='5'><strong>测试步骤 </strong></font></div></td>"
                + "<td><div align='center'><font size='5'><strong>测试描述 </strong></font></div></td>"
                + "<td><div align='center'><font size='5'><strong>预期 </strong></font></div></td>"
                + "<td><div align='center'><font size='5'><strong>结果 </strong></font></div></td>"
                + "<td><div align='center'><font size='5'><strong>是否通过 </strong></font></div></td>"
                + "<td><div align='center'><font size='5'><strong>测试时间 </strong></font></div></td>" + "</tr>";

        fw.append(caseContent);
        fw.close();
    }

    /**
     * 写入单元测试信息
     *
     * @param solo
     * @param checkPoint 测试描述
     * @param expect     结果
     * @throws Exception
     */
    public static void writeCaseReport(Solo solo, String className, String checkPoint, boolean expect)
            throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:s");
        String timeString = sdf.format(new Date());

        File sdCardDir = Environment.getExternalStorageDirectory();
        File path = new File(sdCardDir + "/" + className + ".html");// 创建文件

        String flag = null;//是否通过测试
        if (expect) {
            flag = "pass";
        } else {
            flag = "not pass";
        }

        String caseContent = "<tr>" + "<td><div align='center'><font size='5'><strong>" + PEPORTFILEINDEX
                + " </strong></font></div></td>" + "<td><div align='center'><font size='5'><strong>" + checkPoint
                + "</strong></font></div></td>" + "<td><div align='center'><font size='5'><strong>" + "true"
                + " </strong></font></div></td>" + "<td><div align='center'><font size='5'><strong>" + expect
                + " </strong></font></div></td>" + "<td><div align='center'><font size='5'><strong>" + flag
                + "</strong></font></div></td>" + "<td><div align='center'><font size='5'><strong>" + timeString
                + "</strong></font></div></td>" + "</tr>";

        FileWriter fw = new FileWriter(path.getAbsolutePath(), true);
        fw.append(caseContent);
        fw.close();
        
        PEPORTFILEINDEX++;
    }
}
