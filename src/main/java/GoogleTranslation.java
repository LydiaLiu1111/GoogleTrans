/**
 * Created by liuqing on 2018/2/7.
 */


import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import java.io.*;

/**
 *  Translate Chinese input file into English line by line with Google Trans API.
 *  Features:
 *      - stop & resume: count lines in trans result file, start translation from
 *                       the point where it stopped
 */
public class GoogleTranslation {

    String filePath;
    String outPath;
    String APIKey;
    boolean resume;// if set to false, will clean the output file

    public GoogleTranslation(String[] args){
        String filePath = args[0];
        String outPath = args[1];
        String APIKey =  args[2];
        boolean resume = Boolean.valueOf(args[3]);// if set to false, will clean the output file

        this.filePath = filePath;
        this.outPath = outPath;
        this.APIKey = APIKey;
        this.resume = resume;
    }

    public void translate() throws IOException {


        // Instantiates a client
        //Translate translate = TranslateOptions.getDefaultInstance().getService();
        Translate translate = TranslateOptions.newBuilder().setApiKey(APIKey).build().getService();


        // read in file to translate

        BufferedReader br=null;
        String line=null;
        FileReader fr=new FileReader(filePath);
        br=new BufferedReader(fr);

        // read the trans result file to cnt how many lines have been translated
        int length = 0;
        if(resume){
            // count write file length
            FileReader frWriter=new FileReader(outPath);
            LineNumberReader count = new LineNumberReader(frWriter);
            while (count.skip(Long.MAX_VALUE) > 0)
            {
                // Loop just in case the file is > Long.MAX_VALUE or skip() decides to not read the entire file
            }

            length = count.getLineNumber() ;
            System.out.println("output file length: " + length);
        }


        // write to file
        File outPutFile = new File (outPath);
        outPutFile.createNewFile();

        FileWriter fileWriter = new FileWriter(outPutFile,resume);
        String outPutString = "";

        int cnt = 0;// used to skip translated lines
        int glb_cnt = length;// used to keep track of lines currently being translated

        while((line=br.readLine())!=null){

            // skip the lines that have been translated
            if(resume){
                // continue from previous stop point
                cnt++;
                if(cnt <= length){
                    continue;
                }
            }

            glb_cnt++;

            // The text to translate
            String text = line;

            System.out.print(glb_cnt + " . " + text + "\t:\t");

            // Translates some text into Russian
            Translation translation =
                    translate.translate(
                            text,
                            TranslateOption.sourceLanguage("zh-CN"),
                            TranslateOption.targetLanguage("en"));

            String transRes = translation.getTranslatedText();
            System.out.println(transRes);
            fileWriter.write(transRes + "\n");
            fileWriter.flush();
        }

        fileWriter.close();


    }



    public static void main(String[] args) throws Exception {

        GoogleTranslation GT = new GoogleTranslation(args);
        GT.translate();

    }
}
