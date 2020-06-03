package com.efy.batch.core;

import com.google.common.io.Files;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Date 2019/1/29 17:34
 * @Created by Efy
 * @Description TODO
 */
public class MavenUtil {
    public static String MAVEN_URL = "http://127.0.0.1:8081/nexus/repository/";
    public static String MAVEN_REPOSITORY = "xx";
    public static String MAVEN_HOME = "D:\\soft-url\\apache-maven-3.5.0-bin\\apache-maven-3.5.0\\bin\\";
    public static String MAVEN_BASE = "D:\\soft-url\\m2\\repository\\";
    public static String FILE_PATH = "D:\\codes\\";
    public static StringBuffer FILE_LIST = new StringBuffer();

    private static void cleanCache(File file){
        if(file.isDirectory()) {
            File[] files = file.listFiles((File dir, String name) -> {
                return name.endsWith(".lastUpdated") || dir.isDirectory();
            });
            for(File temp : files){
                cleanCache(temp);
            }
        }
        if(file.getName().endsWith(".lastUpdated")){
//            System.out.println(file.getAbsolutePath());
            file.delete();
        }
    }

    private static void getAllFiles(File file){
        if(file.isDirectory()) {
            File[] files = file.listFiles((File dir, String name) ->
                    name.endsWith(".jar") || name.endsWith(".pom") || dir.isDirectory());
            for(File temp : files){
                getAllFiles(temp);
            }
        }
        File pomWithoutJar = new File(file.getAbsolutePath().replace(".pom",".jar"));
        File jarsPom = new File(file.getAbsolutePath().replace(".jar",".pom"));
        if(file.getName().endsWith(".jar") && !jarsPom.exists()){
            genPom(file);
            getFileListStr(file);
        }else if(file.getName().endsWith(".jar") || (file.getName().endsWith(".pom") && !pomWithoutJar.exists())){
            getFileListStr(file);
        }else{

        }
    }

    private static void genPom(File file){
        String pomContent = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
                "<modelVersion>4.0.0</modelVersion>\n" +
                "<groupId>{groupId}</groupId>\n" +
                "<artifactId>{artifactId}</artifactId>\n" +
                "<version>{version}</version>\n" +
                "<packaging>jar</packaging>\n" +
                "</project>";
        Map<String,String> propMap = getJarProp(file);
        String groupId = propMap.get("groupId");
        String artifactId = propMap.get("artifactId");
        String version = propMap.get("version");
        String pom = propMap.get("pom");
        File jarsPom = new File(pom);
        pomContent = pomContent.replace("{groupId}",groupId);
        pomContent = pomContent.replace("{artifactId}",artifactId);
        pomContent = pomContent.replace("{version}",version);
        writeToFile(jarsPom,pomContent,false);
    }

    private static void getFileListStr(File file){
        Map<String,String> propMap = getJarProp(file);
        String groupId = propMap.get("groupId");
        String artifactId = propMap.get("artifactId");
        String type = propMap.get("type");
        String fileName = propMap.get("fileName");
        String version = propMap.get("version");
        String pom = propMap.get("pom");
        FILE_LIST.append(groupId).append("|");
        FILE_LIST.append(artifactId).append("|");
        FILE_LIST.append(fileName).append("|");
        FILE_LIST.append(version).append("|");
        FILE_LIST.append(pom).append("|");
        FILE_LIST.append(type);
        FILE_LIST.append("\r\n");
//        System.out.println(FILE_LIST);
    }

    private static Map<String,String> getJarProp(File file){
        Map<String,String> propMap = new HashMap<>();
        String filePath = file.getParent().replace(MAVEN_BASE,"");
        String groupId = "";
        String[] temp = filePath.split("\\\\");
        for(int i=0;i<temp.length-2;i++){
            groupId += temp[i];
            if(i < temp.length-3){
                groupId += ".";
            }
        }
        String artifactId = filePath.split("\\\\")[filePath.split("\\\\").length - 2];
        String type = file.getName().substring(file.getName().lastIndexOf(".")+1);
        String fileName = file.getAbsolutePath();
        String version = filePath.split("\\\\")[filePath.split("\\\\").length - 1];
        String pom = file.getAbsolutePath().replace(".jar",".pom");
        propMap.put("groupId",groupId);
        propMap.put("artifactId",artifactId);
        propMap.put("type",type);
        propMap.put("fileName",fileName);
        propMap.put("version",version);
        propMap.put("pom",pom);
        return propMap;
    }

    public synchronized static void writeToFile(File file,String content,boolean append){
        FileOutputStream fos = null;
        try {
            if(!file.exists()){
                file.mkdirs();
                file.delete();
                file.createNewFile();
            }
            fos = new FileOutputStream(file,append);
            fos.write(content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fos != null){
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void genFile(){
        String batName = "deploy.bat";
        String txtName = "jars.txt";
        String content =
            "@echo off\r\n" +
            "for /f \"tokens=1,2,3,4,5,6 delims=|\" %%a in ("+ txtName +") do (\r\n" +
            "    copy %%c temp.%%f\r\n" +
            "    copy %%e temp.pom\r\n" +
            "    mvn deploy:deploy-file " +
                    "-Dpackaging=%%f " +
                    "-DgroupId=%%a " +
                    "-DartifactId=%%b " +
                    "-Dfile=temp.%%f " +
                    "-Dversion=%%d " +
                    "-DpomFile=temp.pom " +
                    "-Durl="+ MAVEN_URL +" " +
                    "-DrepositoryId="+ MAVEN_REPOSITORY +
                    "\r\n" +
            "    del temp.%%f\r\n" +
            "    del temp.pom\r\n" +
            ")\r\n" +
            "pause";
        writeToFile(new File(FILE_PATH + batName),content,false);
        writeToFile(new File(FILE_PATH + txtName),FILE_LIST.toString(),false);
    }

    public static void build(String includePath,boolean batMode){
        cleanCache(new File(MAVEN_BASE));
        System.out.println("缓存清理完成.");
        if(includePath == null){
            //与include算法二选一
            getAllFiles(new File(MAVEN_BASE));
        }else{
            //include算法
            String[] list = includePath.split("\n");
            for(String s : list){
                String[] file = s.split("\\|");
                String groupId,artifactId,version;
                groupId = file[0].trim();
                artifactId = file[1].matches("\\*| ") ? "" : file[1].trim();
                version = file[2].matches("\\*| ") ? "" : file[2].trim();
                String path = groupId.replaceAll("\\.","\\\\") + "\\" + artifactId + "\\" + version;
                System.out.println(path);
                String includeFile = MAVEN_BASE + path;
                getAllFiles(new File(includeFile));
            }
        }
        System.out.println("文件统计完成.");
        if(batMode){
            genFile();
            System.out.println("文件生成完成.");
            System.out.println("基础环境构建完成,请到目录:"+ FILE_PATH +"下,运行bat即可.");
        }
    }

    public static String[] doDeploy(boolean needLog,String[] onlyFetchThis){
        if(FILE_LIST.toString().trim().isEmpty()){
            return null;
        }
        String[] deploedList = readDeployed();
        String[] jarList = null;
        if(onlyFetchThis != null && onlyFetchThis.length > 0){
            jarList= onlyFetchThis;
        }else{
            jarList= FILE_LIST.toString().split("\r\n");
        }
        List<String> succList = new ArrayList<>();
        List<String> failList = new ArrayList<>();
        StringBuffer succSb = new StringBuffer();
        StringBuffer failSb = new StringBuffer();
        for(String jar : jarList){
            if(checkDeployed(jar,deploedList)){
               continue;
            }

            boolean result = doSingleDeploy(jar,needLog);

            if(result){
                succList.add(jar);
                succSb.append(jar).append("\r\n");
            }else{
                failList.add(jar);
                failSb.append(jar).append("\r\n");
            }
        }
        writeToFile(new File(FILE_PATH+"succ.txt"),succSb.toString(),true);
        return failSb.toString().trim().split("\r\n");
    }

    public static boolean doSingleDeploy(String jar,boolean needLog){
        File jarFile = new File(jar.split("\\|")[2]);
        File pomFile = new File(jar.split("\\|")[4]);
        Map<String,String> propMap = getJarProp(jarFile);
        String groupId = propMap.get("groupId");
        String artifactId = propMap.get("artifactId");
        String type = propMap.get("type");
        String version = propMap.get("version");
        File tempJarFile = new File(FILE_PATH+artifactId+"-"+version+"."+type);
        File tempPomFile = new File(FILE_PATH+artifactId+"-"+version+".pom");
        try {
            Files.copy(jarFile,tempJarFile);
            Files.copy(pomFile,tempPomFile);
            String cdCmd = "cd " + MAVEN_HOME + " && ";
            String deployCmd = "mvn -e deploy:deploy-file" +
                    " -Dpackaging=" + type +
                    " -DgroupId=" + groupId +
                    " -DartifactId=" + artifactId +
                    " -Dfile=" + tempJarFile.getAbsolutePath() +
                    " -Dversion=" + version +
                    " -DpomFile=" + tempPomFile.getAbsolutePath() +
                    " -Durl="+ MAVEN_URL +
                    " -DrepositoryId="+ MAVEN_REPOSITORY;
            String cmd = cdCmd + deployCmd;
            boolean result = exeCmd(cmd,needLog);
            System.out.println(artifactId + "-" + version + "." + type + "执行结果:" + result);
            tempJarFile.delete();
            tempPomFile.delete();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void start(boolean needLog){
        String[] failList = null;
        //有失败记录的最多执行3次
        int totalTimes = 3;
        int i = 0;
        for(failList = doDeploy(needLog,failList);
            (failList != null && failList.length > 0) || i < totalTimes;
            failList = doDeploy(needLog,failList),i++){
            if(failList == null || failList.length <= 0 || failList[0].trim().isEmpty()){
                break;
            }
        }
    }

    public static boolean checkDeployed(String jar,String[] deployedList){
        if(deployedList == null || deployedList.length <= 0){
            return false;
        }
        for(String deps : deployedList){
            if(deps.equals(jar)){
                return true;
            }
        }
        return false;
    }

    public static String[] readDeployed(){
        File file = new File(FILE_PATH+"succ.txt");
        if(!file.exists()){
            return null;
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            StringBuffer sb = new StringBuffer();
            String line;
            while((line = br.readLine()) != null){
                sb.append(line).append("\r\n");
            }
            return sb.toString().split("\r\n");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean exeCmd(String commandStr,boolean needLog) {
        BufferedReader infoBr = null;
        BufferedReader errBr = null;
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("cmd /c " + commandStr);
            infoBr = new BufferedReader(new InputStreamReader(p.getInputStream(), Charset.forName("GBK")));
            errBr = new BufferedReader(new InputStreamReader(p.getErrorStream(),Charset.forName("GBK")));
            System.out.println("当前命令:" + commandStr);
            String line = null;
            while ((line = infoBr.readLine()) != null) {
                if(needLog) {
                    System.out.println(line);
                }
            }
            while ((line = errBr.readLine()) != null) {
                if(needLog) {
                    System.out.println(line);
                }
            }
            return p.exitValue() == 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (infoBr != null) {
                    infoBr.close();
                }
                if (errBr != null) {
                    errBr.close();
                }
                if(p != null){
                    p.destroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static void main(String[] args) {
        /**
         * includePath格式groupId|artifactId|version,多个path末尾用\n
         * 例:"org.glassfish.jaxb|jaxb-bom|*"
         * 或 "org.glassfish.jaxb|*|*\n"
         *    "org.glassfish.jaxb111|*|*\n" */
        long tStartTime,tEndTime;
        tStartTime = System.currentTimeMillis();
        //"D:\\soft-url\\m2\\repository\\org\\springframework\\boot\\spring-boot-starter-amqp\\2.0.4.RELEASE\\";
        String includePath = "org.springframework.boot|*|*";
        MavenUtil.build(includePath,true);
        MavenUtil.start(true);

        tEndTime = System.currentTimeMillis();
        double cost = (tEndTime - tStartTime) / 1000;
        System.out.println("总计耗时:" + cost + "s");
    }
}
