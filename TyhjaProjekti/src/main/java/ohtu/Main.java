package ohtu;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
 
public class Main {
 
    public static void main(String[] args) throws IOException {
        String studentNr = "12750500";
        if ( args.length>0) {
            studentNr = args[0];
        }
 
        String url = "http://ohtustats-2013.herokuapp.com/opiskelija/"+studentNr+".json";
 
        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url);
        client.executeMethod(method);
 
        InputStream stream =  method.getResponseBodyAsStream();
 
        String bodyText = IOUtils.toString(stream);
 
//        System.out.println("json-muotoinen data:");
        System.out.println( bodyText );
 
        Gson mapper = new Gson();
        Palautukset palautukset = mapper.fromJson(bodyText, Palautukset.class);
 
//        System.out.println("oliot:");
		System.out.println("opiskelijanumero: "+studentNr+"\n");
		int kokonaisaika = 0;
		int tehtavia = 0;
        for (Palautus palautus : palautukset.getPalautukset()) {
//            System.out.println( palautus );
			System.out.println("viikko "+palautus.getViikko()+": "+palautus.getTehtavia() + 
					" tehtävää "+palautus.getTehtavat()+
					"\t\taikaa kului "+palautus.getTunteja()+" tuntia");
			kokonaisaika += palautus.getTunteja();
			tehtavia += palautus.getTehtavia();
        }
		System.out.println();
		System.out.println("yhteensä "+tehtavia+" tehtävää "+kokonaisaika+" tuntia");
 
    }
}