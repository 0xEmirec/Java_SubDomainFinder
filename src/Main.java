import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main{
    static Scanner klavye = new Scanner(System.in);
    public static void main(String[] args) throws IOException {
        start();
        System.out.print("Target >> ");
        String target = klavye.nextLine();
        Target targetNesne = new Target(target);
        if(target.startsWith("http://")){
            int responseCode = targetNesne.HttpStatusCode(target);
            if (responseCode != 0) {
                targetNesne.SubdomainFind(targetNesne.getTarget());
            }
        }else System.out.println("Hedef adresi yanlış girdiniz!");

        
        System.out.println("Subdomain Sayisi : "+targetNesne.getSubDomainSayi());
        targetNesne.dosyaYaz();
    }
    public static void start(){
        System.out.println("""
                            ************ SUBDOMAIN FINDER ************
                            Target: http://target.com/
                            Github: https://github.com/0xEmirec
                """);
    }
    public static void dosyaOku() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("subdomains.txt"));
        String satir;
        while ((satir = bufferedReader.readLine()) != null){
            String subdomain = "http://"+satir+".google.com";
            System.out.println(subdomain);
        }
        bufferedReader.close();
    }


}

class Target{
    private String target;
    private ArrayList<String> subDomainList;
    private int subDomainSayi;

    public Target(String target) {
        this.target = target;
        subDomainList = new ArrayList<>();
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public ArrayList getSubDomainList() {
        return subDomainList;
    }

    public void getSubDomainListt() {
        for (String i:subDomainList){
            System.out.println(i);
        }
    }

    public int getSubDomainSayi() {
        return subDomainSayi;
    }

    public int HttpStatusCode(String websiteURL){
        int statusCode=0;
        try {
            URL url = new URL(websiteURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            switch (responseCode) {
                case 200:
                    statusCode=200;
                    break;
                case 403:
                    statusCode=403;
                break;
                case 404:
                    statusCode=404;
                    break;
                default:
                    statusCode=responseCode;
                    break;
            }
        }catch (SocketException e){
            System.out.println("Böyle bir website mevcut değil!");
        }catch (IOException e) {
            System.out.println("Website is down or not responding");
        }
        return statusCode;
    }

    public void SubdomainFind(String target) throws IOException {
        String domainName = target.substring(7);
        // BURADA SUBDOMAIN LISTEMIZIN YOLUNU VERİYORUZ
        // ELINIZDE BULUNAN SUBDOMAIN LISTESINI VEREREK PROGRAMIN ONA GORE ARAMA YAPMASINI SAGLAYABILIRSINIZ
        BufferedReader bufferedReader = new BufferedReader(new FileReader("subdomains.txt"));
        String satir;
        while ((satir = bufferedReader.readLine()) != null) {
            String subdomain = "http://" + satir + "." + domainName;
            try {
                URL url = new URL(subdomain);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();
                if (responseCode == 200 || responseCode == 301 || responseCode == 302 || responseCode == 403) {
                    subDomainList.add(subdomain);
                    subDomainSayi++;
                    System.out.println(subdomain);
                }
            }catch(Exception e){}
        }
    }

    public void dosyaYaz() throws IOException {
        // BURADA BULUNAN SUBDOMAIN'LERI TARGET-SUBDOMAINS.TXT ADI KAYDEDIYORUZ
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("target-subdomains.txt"));
        bufferedWriter.write(getTarget()+" - SUBDOMAIN LIST\n****************************************\n");
        for (int i=0;i<subDomainList.size();i++){
            bufferedWriter.write(subDomainList.get(i)+"\n");
        }
        bufferedWriter.close();
    }
}
