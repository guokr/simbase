package com.guokr.simbase.benchmarks;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Date;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.guokr.simbase.SimConfig;
import com.guokr.simbase.TestableCallback;
import com.guokr.simbase.engine.SimEngineImpl;
import com.guokr.simbase.events.VectorSetListener;
import com.guokr.simbase.store.VectorSet;

public class MemoryBenchmark {

    public static long start       = -1;
    public static long accumulated = 0;

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {

        SimConfig config = null;
        try {
            Yaml yaml = new Yaml();
            config = new SimConfig((Map<String, Object>) yaml.load(new FileReader("config/simbase.yaml")));
        } catch (FileNotFoundException e) {
        }

        SimEngineImpl engine = new SimEngineImpl(config.getSub("engine"));

        String[] components = { "corsages", "Charbray", "FDRs", "bedbugs", "Ws", "Sasha", "Jerrys", "Bismarks",
                "Tadzhik", "appall", "accounted", "Acs", "chemical", "besotting", "Idahoans", "Sunbelt", "Ugandans",
                "Amish", "Tropicanas", "Pamelas", "Nash", "bloodies", "Paradise", "addicted", "Rios", "Ollie", "beryl",
                "coconuts", "Firestones", "Thoroughbred", "Elwood", "casting", "America", "attorneys", "Claibornes",
                "Puritanisms", "Comanches", "Cenozoic", "Azerbaijani", "abdication", "assaults", "barf", "Zimbabweans",
                "Elise", "commonly", "brutally", "cheaters", "connection", "bounds", "Terkel", "Geffens",
                "compressions", "NyQuil", "condensation", "Samarkand", "Walgreens", "July", "bedfellow", "colloid",
                "Pakistanis", "Vegemites", "apostates", "appliqué", "Coleman", "Yunnan", "Unitarians",
                "Congregationalists", "bacilluss", "Capistrano", "adequacy", "Colleens", "Unukalhais", "Michel",
                "breaststrokes", "Lipscombs", "Ra", "condors", "Cleos", "Florida", "Merritt", "archangel", "buffeting",
                "Krishnamurti", "Scipio", "birettas", "Kislev", "conversationalists", "carded", "Georges", "cassinos",
                "argosys", "Daltons", "Risorgimento", "Sumatras", "brethren", "carbonates", "Ramonas", "Chesterfield",
                "Tuts", "brigandage", "conscripting", "claptrap", "anterior", "Durant", "Sachss", "Burmeses",
                "Osborns", "Adonis", "boozy", "awhile", "Brobdingnagian", "cases", "commencement", "bandoleers",
                "bedsteads", "Afrikaners", "afforest", "Oslos", "Carboloy", "counterrevolution", "compliance", "Dick",
                "backspace", "bulged", "Lavoisiers", "cartwheels", "accosting", "abbreviate", "Psyches", "conqueror",
                "Accentures", "choleras", "acidic", "ATMs", "Carrillos", "antitoxin", "betterment", "boyishnesss",
                "alibis", "anchorwomans", "competitivenesss", "convivialitys", "Persias", "boards", "Havoline", "Jobs",
                "Mott", "boasts", "Woodrow", "acrimony", "Chihuahuas", "Karinas", "anarchism", "barbarian", "Rydbergs",
                "annoyed", "Zedong", "abbreviations", "aglitter", "Arnulfos", "Monicas", "Indochinas", "breeders",
                "Coys", "amnesty", "Donetsks", "Maxs", "Vladivostoks", "completions", "burial", "Galibi", "associates",
                "Bruno", "Jaimes", "Antiguas", "costings", "baddest", "addicts", "corpulent", "cocking", "Mos",
                "collapsible", "Namibia", "Marylou", "coeducations", "Enifs", "Ellesmeres", "arroyos", "conceptions",
                "captors", "cots", "cookys", "confess", "Nagpur", "addressees", "conjunctures", "bursting", "Krishnas",
                "clipt", "apologists", "benzenes", "blurb", "Joann", "bosn", "clubfoot", "affiliates", "blotchier",
                "ayatollahs", "Gordian", "India", "Pigmy", "chemotherapy", "Cm", "Diems", "conglomerated", "cartwheel",
                "Melvilles", "apposite", "consents", "Streisands", "Hannibals", "clips", "Buddys", "Larry", "Graffias",
                "chimeras", "Hegels", "clue", "ESTs", "admixture", "asymmetrical", "Brahmin", "chevron", "Nobelist",
                "Euclids", "agonizes", "Jeds", "Jacquard", "cartridge", "Lardner", "Anchorage", "amendments",
                "canoeist", "condensations", "coquettes", "Genoas", "Tuamotu", "acceded", "cognomina", "breech",
                "again", "Dunns", "checkout", "chaperoned", "broadlooms", "Shintos", "Unions", "Minos", "Corleones",
                "collection", "bidets", "Nobles", "Juliettes", "Jamaals", "colonize", "Venuss", "byes", "Rapunzels",
                "broccolis", "Elvias", "Messerschmidt", "aesthetic", "Brusselss", "Ahabs", "bourgeoisie", "connecter",
                "complained", "Kirkpatrick", "Jagiellon", "Utah", "concedes", "commemoration", "biographers",
                "Gregorios", "concealments", "cant", "Haywood", "astronomers", "baubles", "Colombians", "Sinhalese",
                "Garfields", "Sondheim", "Iliads", "Samars", "coding", "Democritus", "Quaker", "Andrews",
                "anchorperson", "Doreen", "capitulate", "contraband", "barrels", "Pentiums", "Commons", "corduroy",
                "Gladstone", "Sheba", "canoeists", "caseins", "Lews", "acceptance", "burglars", "Dinah", "ancienter",
                "archaically", "cornstarch", "Intels", "Kristinas", "blatantly", "Bartholomew", "Colorado", "confined",
                "Horn", "Dzerzhinskys", "Amazons", "Cossack", "Trippe", "carcinogenics", "Marianos", "Schedar",
                "apparitions", "anteaters", "commonest", "accusatory", "Varese", "competences", "Cleopatras",
                "Dejesuss", "Jeannettes", "component", "Prensas", "Cheryl", "bottlenecks", "basketballs", "Malthuss",
                "Aeneids", "Batu", "contriving", "burnouses", "breeze", "Americanized", "Bukhara", "burnooses",
                "bigamy", "canes", "ammunition", "Miss", "connector", "agglomerations", "burred", "aspirins", "calmed",
                "Lyndas", "airdrops", "climbed", "Ouijas", "balks", "Ghazvanid", "Lesotho", "Wheeling", "Nichiren",
                "ballyhoo", "Bernadines", "Jewell", "Georgian", "Noriegas", "Yossarian", "Nunez", "conquest",
                "Cruises", "Munichs", "capable", "Nevada", "angioplastys", "Benet", "Aruba", "Guadeloupes", "Swedish",
                "colonizations", "Acruxs", "Bostons", "Chianti", "apace", "actively", "Riddles", "advertisements",
                "Myanmar", "Kirchner", "Malcolm", "Paracelsus", "chickadees", "arson", "cavalcades", "broach",
                "Episcopalians", "Debby", "calabashes", "buttons", "clenched", "Lolas", "closes", "blasphemes", "bits",
                "Rorschach", "avoirdupoiss", "Gracie", "constructions", "Legrees", "Langerhans", "Farley",
                "agglomerate", "Stolypin", "blackboards", "cannoning", "Carboloys", "bowlers", "Fitchs", "buckeye",
                "Motrins", "Beaufort", "adventuresome", "Iqaluit", "belling", "countries", "attacks", "berms",
                "Maryannes", "bombastic", "Communists", "Brittneys", "admissible", "Jackys", "bunchs", "Japuras",
                "bangs", "admittances", "aerobaticss", "Kristi", "Waldemar", "compatibles", "Toledo", "Murray",
                "clogs", "buttresss", "Limoges", "Alaskan", "challenger", "Serenas", "bannss", "cods", "McCall",
                "Soviet", "audibilitys", "Wesleys", "bylines", "aquamarines", "Hunt", "blissfully", "beauticians",
                "bosses", "accomplish", "burnout", "Paiges", "barbarians", "Katherine", "Rubbermaids", "Kareninas",
                "boor", "babysit", "cognates", "Kelly", "believers", "Sears", "Livia", "Afros", "conversation",
                "abeyances", "Hoffmans", "brontosauruses", "advisors", "Lavoisier", "Peel", "London", "bikers",
                "Hewletts", "cogitating", "Tbilisis", "averted", "awarded", "Macbeth", "altercations", "Afrikaans",
                "christening", "baste", "contaminate", "Hilario", "Wolfgang", "consolidations", "acrostic",
                "Mormonisms" };

        try {
            engine.bmk(TestableCallback.noop(), "base", components);
            Thread.sleep(100);
            engine.vmk(TestableCallback.noop(), "base", "article");
            Thread.sleep(100);
            engine.rmk(TestableCallback.noop(), "article", "article", "cosinesq");
            Thread.sleep(100);

            VectorSetListener listener = new TestMemListener();
            engine.listen("article", listener);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        start = new Date().getTime();
        for (int i = 1; i <= 10000; i++) {

            float total = 0;
            float[] distr = new float[512];
            for (int j = 0; j < 512; j++) {
                distr[j] = (float) Math.random();
                total += distr[j];
            }
            for (int j = 0; j < 512; j++) {
                distr[j] = distr[j] / total;
            }

            engine.vadd(TestableCallback.noop(), "article", i, distr);

        }
    }

    private static class TestMemListener implements VectorSetListener {

        private int counter = 0;

        @Override
        public void onVectorAdded(VectorSet evtSrc, int vecid, float[] vector) {
            if (counter % 1000 == 0) {
                long duration = new Date().getTime() - start;
                System.out.println("vecid:" + counter + "\tmemory:" + Runtime.getRuntime().totalMemory() + "\ttime:"
                        + (duration / 1000));
                start = new Date().getTime();
            }
            counter++;
        }

        @Override
        public void onVectorAdded(VectorSet evtSrc, int vecid, int[] vector) {
        }

        @Override
        public void onVectorSetted(VectorSet evtSrc, int vecid, float[] old, float[] vector) {
        }

        @Override
        public void onVectorSetted(VectorSet evtSrc, int vecid, int[] old, int[] vector) {
        }

        @Override
        public void onVectorAccumulated(VectorSet evtSrc, int vecid, float[] vector, float[] accumulated) {
        }

        @Override
        public void onVectorAccumulated(VectorSet evtSrc, int vecid, int[] vector, int[] accumulated) {
        }

        @Override
        public void onVectorRemoved(VectorSet evtSrc, int vecid) {
        }

    }
}
