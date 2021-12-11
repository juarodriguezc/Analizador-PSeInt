import java.sql.SQLOutput;
import java.util.*;

class Token {
    public int fila;
    public int columna;
    public String lexema="";

    public String tipo;


    public static Map<String, String> dicTipo = new HashMap<String, String>() {{
        //Palabras reservadas
        put("algoritmo" , "algoritmo");
        put("finalgoritmo" , "finalgoritmo");
        put("proceso" , "proceso");
        put("finproceso" , "finproceso");
        put("funcion" , "funcion");
        put("finfuncion" , "finfuncion");
        put("subproceso" , "subproceso");
        put("finsubproceso" , "finsubproceso");
        put("subalgoritmo" , "subalgoritmo");
        put("finsubalgoritmo" , "finsubalgoritmo");
        put("definir" , "definir");
        put("como" , "como");
        put("numerico" , "numerico");
        put("numero" , "numero");
        put("entero" , "entero");
        put("real" , "real");
        put("caracter" , "caracter");
        put("cadena" , "cadena");
        put("logico" , "logico");
        put("texto" , "texto");
        put("verdadero" , "verdadero");
        put("falso" , "falso");
        put("leer" , "leer");
        put("escribir" , "escribir");
        put("esperar" , "esperar");
        put("si" , "si");
        put("entonces" , "entonces");
        put("sino" , "sino");
        put("finsi" , "finsi");
        put("segun" , "segun");
        put("hacer" , "hacer");
        put("de" , "de");
        put("otro" , "otro");
        put("modo" , "modo");
        put("finsegun" , "finsegun");
        put("mientras" , "mientras");
        put("finmientras" , "finmientras");
        put("repetir" , "repetir");
        put("hasta" , "hasta");
        put("que" , "que");
        put("dimension" , "dimension");
        put("para" , "para");
        put("hasta" , "hasta");
        put("con" , "con");
        put("paso" , "paso");
        put("finpara" , "finpara");
        put("borrar" , "borrar");
        put("limpiar" , "limpiar");
        put("pantalla" , "pantalla");
        put("esperar" , "esperar");
        put("tecla" , "tecla");
        put("segundos" , "segundos");
        put("milisegundos" , "milisegundos");
        //Tipos de datos
        put("token_entero" , "token_entero");
        put("token_real" , "token_real");
        put("token_cadena" , "token_cadena");
        //Operadores y símbolos
        put("o" , "token_o");
        put("|" , "token_o");
        put("y" , "token_y");
        put("&" , "token_y");
        put("no" , "token_neg");
        put("~" , "token_neg");
        put("mod" , "token_mod");
        put("%" , "token_mod");
        put("=" , "token_igual");
        put("<-" , "token_asig");
        put("<>" , "token_dif");
        put("<" , "token_menor");
        put(">" , "token_mayor");
        put("<=" , "token_menor_igual");
        put(">=" , "token_mayor_igual");
        put("+" , "token_mas");
        put("-" , "token_menos");
        put("/" , "token_div");
        put("*" , "token_mul");
        put(";" , "token_pyc");
        put(":" , "token_dosp");
        put("(" , "token_par_izq");
        put(")" , "token_par_der");
        put("[" , "token_cor_izq");
        put("]" , "token_cor_der");
        put("," , "token_coma");
        put("^" , "token_pot");
        //EOF
        put("$" , "$");
        //Test
        put("uno" , "uno");
        put("dos" , "dos");
        put("tres" , "tres");
        put("cuatro" , "cuatro");
    }};

    public Token(String  tipo, int fila, int columna) {
        if(dicTipo.get(tipo) == null){
            this.tipo = "id";
            this.lexema = tipo;
        }else{
            this.tipo = dicTipo.get(tipo);
        }
        this.fila = fila;
        this.columna = columna;
    }

    public Token(String tipo, String lexema, int fila, int columna) {
        this.tipo = dicTipo.get(tipo);
        this.lexema = lexema;
        this.fila = fila;
        this.columna = columna;
    }

    @Override
    public String toString() {
        if (lexema.equals("") && !tipo.equals("token_cadena")){
            return "<" +
                    tipo + "," +
                    fila + "," +
                    columna +
                    ">";
        }
        return "<" +
                tipo + "," +
                lexema + "," +
                fila + "," +
                columna +
                ">";
    }
}

class LexAn {
    private enum states{
        q0, q1, q2, q3, q4, q5, q6, q7, q8
    }
    private ArrayList<Token> tokenList;
    public ArrayList<Token> analize(List<String> input) {
        ArrayList result = new ArrayList<Token>();
        for (int i = 0; i < input.size(); i++) {
            String line = input.get(i);
            result.addAll(analizeLine(line, i));
        }
        tokenList = result;
        tokenList.add(new Token("$",0,0));
        return tokenList;
    }

    public ArrayList<Token> analizeLine(String line, int nLine) {
        ArrayList result = new ArrayList<>();
        char[] charArray = (line + "   ").toCharArray();
        //Inicializar estado en q0
        states currentState = states.q0;
        //Posicion inicial de lectura
        int pStart = 0;
        //Cadena guardada
        String cadena = "";
        for (int i = 0; i < charArray.length; i++){
            char input = charArray[i];
            cadena = cadena + input;
            switch (currentState){
                case q0:
                    if((input >= 'a' && input <= 'z') || (input >= 'A' && input <= 'Z')){
                        currentState = states.q1;
                    }
                    else if(input == ' '){
                        cadena = removeLastChars(cadena,1);
                        pStart += 1;
                    }
                    else if((input >= '0' && input <= '9')){
                        currentState = states.q2;
                    }
                    else if(input == '\"' || input == '\''){
                        cadena = removeLastChars(cadena,1);
                        pStart += 1;
                        currentState = states.q5;
                    }
                    else if(input == '/'){
                        currentState = states.q6;
                    }
                    else if(input == '<'){
                        currentState = states.q7;
                    }
                    else if(input == '>'){
                        currentState = states.q8;
                    }
                    else{
                        Token tok = new Token(cadena,nLine+1,pStart+1);
                        //Verificar que el simbolo ingresado pertenezca
                        if(tok.tipo.equals("id")){
                            //Simbolo no existente
                            System.out.println(">>> Error lexico (linea: "+(nLine+1)+", posicion: "+(pStart+1)+")");
                            System.exit(0);
                        }
                        else{
                            //Añadir token a la lista
                            result.add(tok);
                            //System.out.println(tok.toString());
                            //Actualizar valores del iterador
                            pStart = i + 1;
                            //Reiniciar los valores
                            cadena = "";
                            currentState = states.q0;
                        }
                    }
                    break;
                case q1:
                    if((input >= 'a' && input <= 'z') || (input >= 'A' && input <= 'Z') || (input >= '0' && input <= '9') || input == '_'){
                        currentState = states.q1;
                    }
                    else{
                        //Estado de aceptación ID O PALABRA RESERVADA
                        {
                            //Remover el último caracter
                            cadena = removeLastChars(cadena,1);
                            //Crear token de ID o Palabra reservada
                            Token tok = new Token(cadena.toLowerCase(),nLine+1,pStart+1);
                            //Añadir token a la lista
                            result.add(tok);
                            //System.out.println(tok.toString());
                            //Actualizar los valores del iterador
                            pStart = i;
                            i--;
                            //Reiniciar los valores
                            cadena = "";
                            currentState = states.q0;
                        }
                    }
                    break;
                case q2:
                    if((input >= '0' && input <= '9')){
                        currentState = states.q2;
                    }
                    else if(input == '.'){
                        currentState = states.q3;
                    }
                    else if(isIntOperator(input)|| input == ';'|| input == ' '){
                        //Estado de aceptación ENTERO
                        {
                            //Remover el último caracter
                            cadena = removeLastChars(cadena,1);
                            //Crear token de ENTERO
                            Token tok = new Token("token_entero",cadena,nLine+1,pStart+1);
                            //Añadir token a la lista
                            result.add(tok);
                            //System.out.println(tok.toString());
                            //Actualizar
                            pStart = i;
                            i-=1;
                            //Reiniciar los valores
                            cadena = "";
                            currentState = states.q0;
                        }
                    }
                    else{
                        Token tok = new Token("error_lexico",nLine+1,pStart+1);
                        //Simbolo no existente
                        System.out.println(">>> Error lexico (linea: "+(nLine+1)+", posicion: "+(pStart+1)+")");
                        System.exit(0);
                    }
                    break;
                case q3:
                    if((input >= '0' && input <= '9')){
                        currentState = states.q4;
                    }
                    else{
                        //Estado de aceptación ENTERO
                        {
                            //Remover ultimos dos caracteres
                            cadena = removeLastChars(cadena,2);
                            //Crear token de ENTERO
                            Token tok = new Token("token_entero",cadena,nLine+1,pStart+1);
                            //Añadir token a la lista
                            result.add(tok);
                            //System.out.println(tok.toString());
                            //Actualizar valores del iterador
                            pStart = i - 1;
                            i-=2;
                            //Reiniciar los valores
                            cadena = "";
                            currentState = states.q0;
                        }
                    }
                    break;
                case q4:
                    if((input >= '0' && input <= '9')){
                        currentState = states.q4;
                    }
                    else if(isIntOperator(input) || input == ';'|| input == ' '){
                        //Estado de aceptación REAL
                        {
                            //Remover ultimos caracter
                            cadena = removeLastChars(cadena,1);
                            //Crear token de ENTERO
                            Token tok = new Token("token_real",cadena,nLine+1,pStart+1);
                            //Añadir token a la lista
                            result.add(tok);
                            //System.out.println(tok.toString());
                            //Actualizar
                            pStart = i;
                            i-=1;
                            //Reiniciar los valores
                            cadena = "";
                            currentState = states.q0;
                        }
                    }
                    else{
                        Token tok = new Token("error_lexico",nLine+1,pStart+1);
                        //Simbolo no existente
                        System.out.println(">>> Error lexico (linea: "+(nLine+1)+", posicion: "+(pStart+1)+")");
                        tok.tipo = "error_lexico";
                        tok.lexema = "error_lexico";
                        result.add(tok);
                        return result;
                    }
                    break;
                case q5:
                    if(input != '\"' && input != '\''){
                        if(i == (charArray.length - 4)){
                            Token tok = new Token("error_lexico",nLine+1,pStart);
                            //Simbolo no existente
                            System.out.println(">>> Error lexico (linea: "+(nLine+1)+", posicion: "+(pStart)+")");
                            tok.tipo = "error_lexico";
                            tok.lexema = "error_lexico";
                            result.add(tok);
                            return result;
                        }
                        currentState = states.q5;
                    }
                    else{
                        //Estado de aceptación CADENA
                        {
                            //Remover ultimos caracter
                            cadena = removeLastChars(cadena,1);
                            //Crear token de CADENA
                            Token tok = new Token("token_cadena",cadena,nLine+1,pStart);
                            //Añadir token a la lista
                            result.add(tok);
                            //System.out.println(tok.toString());
                            //Actualizar valores del iterador
                            pStart = i+1;
                            //Reiniciar los valores
                            cadena = "";
                            currentState = states.q0;
                        }
                    }
                    break;
                case q6:
                    if(input == '/'){
                        return new ArrayList<>();
                    }else{
                        //Estado de aceptación DIVISION
                        {
                            //Remover ultimos caracter
                            cadena = removeLastChars(cadena,1);
                            //Crear token de DIVISION
                            Token tok = new Token(cadena,nLine+1,pStart+1);
                            //Añadir token a la lista
                            result.add(tok);
                            //System.out.println(tok.toString());
                            //Actualizar valores del iterador
                            pStart = i;
                            i-=1;
                            //Reiniciar los valores
                            cadena = "";
                            currentState = states.q0;
                        }
                    }
                    break;
                case q7:
                    if(input == '>' || input == '=' || input == '-'){
                        //Estado de aceptación DIFERENTE o MENOR_IGUAL o ASIGNACION
                        {
                            //Crear token
                            Token tok = new Token(cadena,nLine+1,pStart+1);
                            //Añadir token a la lista
                            result.add(tok);
                            //System.out.println(tok.toString());
                            //Actualizar valores del iterador
                            pStart = i + 1;
                            //Reiniciar los valores
                            cadena = "";
                            currentState = states.q0;
                        }
                    }
                    else{
                        //Estado de aceptación MENOR
                        {
                            //Remover ultimos caracter
                            cadena = removeLastChars(cadena,1);
                            //Crear token de MENOR
                            Token tok = new Token(cadena,nLine+1,pStart+1);
                            //Añadir token a la lista
                            result.add(tok);
                            //System.out.println(tok.toString());
                            //Actualizar valores del iterador
                            pStart = i;
                            i-=1;
                            //Reiniciar los valores
                            cadena = "";
                            currentState = states.q0;
                        }
                    }
                    break;
                case q8:
                    if(input == '='){
                        //Estado de aceptación MAYOR_IGUAL
                        {
                            //Crear token
                            Token tok = new Token(cadena,nLine+1,pStart+1);
                            //Añadir token a la lista
                            result.add(tok);
                            //System.out.println(tok.toString());
                            //Actualizar valores del iterador
                            pStart = i + 1;
                            //Reiniciar los valores
                            cadena = "";
                            currentState = states.q0;
                        }
                    }
                    else{
                        //Estado de aceptación MAYOR
                        {
                            //Remover ultimos caracter
                            cadena = removeLastChars(cadena,1);
                            //Crear token de MENOR
                            Token tok = new Token(cadena,nLine+1,pStart+1);
                            //Añadir token a la lista
                            result.add(tok);
                            //System.out.println(tok.toString());
                            //Actualizar valores del iterador
                            pStart = i;
                            i-=1;
                            //Reiniciar los valores
                            cadena = "";
                            currentState = states.q0;
                        }
                    }
                    break;
            }
        }
        return result;
    }
    public static String removeLastChars(String str, int chars) {
        return str.substring(0, str.length() - chars);
    }
    public static boolean isIntOperator(char input){
        return (input == '|' || input == '&' || input == '~' || input == '%' || input == '+' || input == '-' || input == '*'|| input == '/'|| input == ':' || input == ',' || input == '('|| input == ')'|| input == '['|| input == ']'|| input == '^' || input == '<'|| input == '>' || input == '=' || input == '\'' || input == '\"');
    }
    public void printTokens(){
        for (Token tok : tokenList) {
            System.out.println(tok.toString());
        }
    }

    public ArrayList<Token> getTokenList() {
        return tokenList;
    }
}

class PredLine{
    public String noTer;
    public HashSet<String> pred;

    public PredLine(String noTer) {
        this.noTer = noTer;
        pred = new HashSet<String>();
    }

    @Override
    public String toString() {
        return "PredLine{" +
                "noTer='" + noTer + '\'' +
                ", pred=" + pred +
                '}';
    }
}

class Grammar{
    //Mayuscula no terminales
    //Minuscula terminales
    private String grammar =
            "A -> B uno $ \n"+
            "A -> dos $ \n"+
            "B -> tres \n"+
            "B -> cuatro A\n"
            ;


    private PredLine[] pred;
    private String[][] transGrammar;
    private PredLine[] sigs;
    private HashSet<String> noTers = new HashSet<>();
    public void loadGrammar(String grammar){
        this.grammar = grammar;
    }
    public void printGrammar(){
        System.out.println(grammar);
    }

    public void analizeGrammar(){
        String[] gramm = grammar.split("\n");
        //Crear arreglo de objetos de prediccion
        pred = new PredLine[gramm.length];
        //Crear matriz con entrada transformada
        transGrammar = new String[gramm.length][];
        for (int i = 0; i < gramm.length; i++){
            String[] line = gramm[i].split("->");
            //Crear set de cada linea de prediccion
            pred[i] = new PredLine(line[0]);
            noTers.add(line[0]);
            //Realizar el análisis de cada linea
            line = gramm[i].split("\\s+");
            //Add linea transformada a la matriz
            transGrammar[i] = line;
        }
        //Crear arreglo de siguientes
        Object[] arr = noTers.toArray();
        sigs = new PredLine[arr.length];
        for(int i = 0; i< arr.length; i++){
            sigs[i] = new PredLine(arr[i].toString().trim());
        }
        for (int i = 0; i < gramm.length; i++){
            //Pos 0 - No terminal  Pos 2 - n  Alpha
            /*pred[i].pred = Primeros(transGrammar[i],2);
            //pred[i].pred = Siguientes(transGrammar[i][0], new HashSet<String>());
            */
            pred[i].pred = Pred(transGrammar[i]);
        }
        //Imprimir resultados
        for(PredLine line : pred){
            System.out.println(line);
        }

    }
    public HashSet<String> Pred(String[] line){
        int indStart = 2;
        HashSet<String> temp = Primeros(line,indStart);
        if(temp.contains("e")){
            HashSet<String> result = Primeros(line, indStart);
            result.remove("e");
            result.addAll(Siguientes(line[0], new HashSet<String>()));
            return result;
        }
        return Primeros(line,indStart);
    }
    public HashSet<String> Primeros(String[] line, int indStart){
        HashSet<String> prim = new HashSet<>();
        if(line[indStart].equals("e")){
            prim.add("e");
            return prim;
        }
        //Revisa si se trata de un no terminal-Mayuscula o terminal-Minuscula
        if(isLowerCase(line[indStart])){
            prim.add(line[indStart]);
            return prim;
        }
        else{
            //Add Primeros(a1) - {e}
            HashSet<String> temp = Primeros(line[indStart]);
            prim.addAll(temp);
            prim.remove("e");
            //Revisar si e pertenece a Primeros(a1)
            if(temp.contains("e")){
                /*
                System.out.println("Primeros: ");
                System.out.println("IndStart: "+indStart);
                System.out.println("Size: "+line.length);
                for(String str : line){
                    System.out.print(str + " ");
                }
                System.out.println();
                */
                if(line.length-1 - indStart == 0){
                    prim.add("e");
                }else if(line.length-1 - indStart > 0){
                    prim.addAll(Primeros(line, indStart+1));
                }
            }
            /*
            System.out.print("Prim( ");
            for(int i = 2; i < line.length; i++){
                System.out.print(line[i]+" ");
            }
            System.out.print(") = "+prim);
             */
            return prim;
        }
    }
    public HashSet<String> Primeros(String word){
        HashSet<String> prim = new HashSet<>();
        /*System.out.println();
        System.out.println("Primeros: "+word);
         */
        for(String[] line : transGrammar){
            if(word.equals(line[0])){
                /*
                System.out.print(line[0]+" -> ");
                for(int i = 2; i< line.length; i++){
                    System.out.print(line[i]+ " ");
                }

                 */
                if(line[0].equals(line[2])){
                    prim.addAll(Primeros(line, 3));
                }else{
                    prim.addAll(Primeros(line, 2));
                }
            }
        }
        return prim;
    }
    public HashSet<String> Siguientes(String noTer, HashSet<String> prev){
        //System.out.println("NoTer: "+noTer);
        //Search noTer in array
        int index;
        for(index = 0; index<sigs.length; index++){
            if(sigs[index].noTer.equals(noTer))break;
        }
        //System.out.print("---*  ");
        //System.out.println(sigs[index]);
        //System.out.println();
        //Check simbolo inicial
        if(noTer.equals("S"))sigs[index].pred.add("$");
        for(String[] line : transGrammar){
            for(int i = 2; i < line.length; i++){
                if(line[i].equals(noTer)){
                    /*
                    for(String str : line){
                        System.out.print(str + " ");
                    }
                    System.out.println();
                    System.out.println("CALCULO DE SIGUIENTES: ");
                    System.out.println("I: "+i+" -  Size: "+(line.length-1));
                    */
                    //Verificar si B = e
                    HashSet<String> temp = new HashSet<>();
                    if(line.length-1 - i > 0){
                        temp = Primeros(line, i+1);
                        sigs[index].pred.addAll(temp);
                        sigs[index].pred.remove("e");

                    }
                    if(temp.contains("e") || line.length-1 - i == 0){
                        /*
                        System.out.println();
                        System.out.println("Punto del error, valor siguientes: "+line[0]);
                        System.out.println("Valor referencia: "+line[i]);
                         */
                        if(!prev.contains(line[0])){
                            prev.add(line[0]);
                            sigs[index].pred.addAll(Siguientes(line[0],prev));
                        }
                    }
                }
            }
        }
        //System.out.println("Sig( "+noTer+" ) = "+sigs[index].pred);
        //System.out.println("----------------");
        return sigs[index].pred;
    }
    public boolean isLowerCase (String str){
        char[] charArray = str.toCharArray();
        for(int i = 0; i < charArray.length; i++){
            if(!Character.isLowerCase( charArray[i])) return false;
        }
        return true;
    }

    public PredLine[] getPred() {
        return pred;
    }

    public String[][] getTransGrammar() {
        return transGrammar;
    }

    public HashSet<String> getNoTers() {
        return noTers;
    }
}

class SintAn {
    private String[][] grammar;
    private PredLine[] pred;
    private ArrayList<Token> tokenList;

    private int index;

    public SintAn(String[][] grammar, PredLine[] pred, ArrayList<Token> tokenList) {
        this.grammar = grammar;
        this.pred = pred;
        this.tokenList = tokenList;
        index = 0;
    }

    public void analizeSintax(){
        if(tokenList.size() > 0){
            funcion("A");
            System.out.println("TOken: "+tokenList.get(index));
            if(!tokenList.get(index).tipo.equals("$")){
                errorSintaxis(tokenList.get(index).tipo);
            }
            System.out.println("El analisis sintactico ha finalizado exitosamente.");
            System.out.println(tokenList.get(index));

        }
    }

    public void funcion(String noTer){
        Token tok = tokenList.get(index);
        boolean contains = false;
        for(int i=0; i<grammar.length; i++){
            if(grammar[i][0].equals(noTer)){
                if(pred[i].pred.contains(tok.tipo)){
                    contains = true;
                    for(int j=2; j<grammar[i].length; j++){
                        if(isLowerCase(grammar[i][j])){
                            emparejar(grammar[i][j]);
                        }else{
                            funcion(grammar[i][j]);
                        }
                    }
                }

                System.out.println("  Pred: "+pred[i].pred);
            }
        }

        if(!contains)errorSintaxis(tok.tipo);
    }

    public void emparejar(String tokEsperado){
        Token tok = tokenList.get(index);
        System.out.println("Index: "+index+ " MaxIndex: "+(tokenList.size()-1));
        System.out.println("Se esperaba: '"+tokEsperado+"'   -    se recibio: '"+tok.tipo+"'");
        if(tok.tipo.equals(tokEsperado)){
            //Get next token
            if(index < tokenList.size()-1)index = index+1;
        }else{

            errorSintaxis(tok.tipo);
        }
    }

    public void errorSintaxis(String token){
        System.out.println("Error sintaxis");
        System.out.println("Token recibido: "+token);
        System.exit(0);
    }

    public boolean isLowerCase (String str){
        char[] charArray = str.toCharArray();
        for(int i = 0; i < charArray.length; i++){
            if(!Character.isLowerCase( charArray[i])) return false;
        }
        return true;
    }
}
public class Main {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        List<String> lineList = new LinkedList<String> ();
        LexAn lexical = new LexAn();
        while (scanner.hasNextLine())
        {
            String line = scanner.nextLine();
            if(line.equals("ñ"))break;
            lineList.add(line);
        }
        scanner.close();
        //Lexical analizer
        lexical.analize(lineList);
        lexical.printTokens();
        //Syntax analizer
        System.out.println("El analisis sintactico ha finalizado exitosamente.");
        Grammar gram = new Grammar();
        gram.printGrammar();
        gram.analizeGrammar();

        System.out.println(gram.getNoTers());

        SintAn sintax = new SintAn(gram.getTransGrammar(), gram.getPred(), lexical.getTokenList());
        sintax.analizeSintax();



    }
}
