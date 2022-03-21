import java.util.*;

/**
 *  @Author Lucas Alves - Disciplina de Sistemas Operacionais - PRÁTICA DE GERENCIAMENTO DE PROCESSOS - UFRPE
  */

public class Principal {
    public static Random gerador = new Random();

    //cores
    public static final String ANSI_VERMELHO = "\u001B[31m";
    public static final String ANSI_RST = "\u001B[0m";
    public static final String ANSI_VERDE = "\u001B[32m" ;
    public static final String ANSI_YELLOW = "\u001B[33m" ;
    public static final String ANSI_CIAN = "\u001B[36m" ;
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";


    public static final long TEMPO_CLOCK= 50; // em ms
    public static final long QUANTUM = 400; // em ms
    private static final int PORCENTAGEM_TERMINO= 4 ; // em  %
    private static final int PORCENTAGEM_BLOQUEIO= 10 ; // em  %
    private static final int PORCENTAGEM_DESBLOQUEIO= 15 ; // em  %


    public static long relogio=0L;

    static  ArrayList<Processo> finalizados= new ArrayList<>();
    static  ArrayList<Processo> bloqueados= new ArrayList<>();

    static PriorityQueue<Processo> prontos= new PriorityQueue<>(); // Escalonamento por prioridade
    static Queue<Processo> prontosRR = new LinkedList<>();// Round-Robin


    static boolean entrada_plus = true;

    public static synchronized void  main(String[] args) {

        /*
         * Prioridade nesse meu caso, imita os processos UNIX onde esse número significa GENEROSIDADE, quanto maior
         * este número, mais generoso é o processo, deixando processos menos generosos passando na sua frente.
         */
        Processo p1=new Processo("chrome", 13, QUANTUM - 100);
        Processo p2=new Processo("daemon-d2", 5, QUANTUM - 200);
        Processo p3=new Processo("email-messag", 3, QUANTUM);
        Processo p4=new Processo("xorg", 7, QUANTUM);


        Processo p5=new Processo("dbus-daemon", 1, QUANTUM + 100);
        Processo p6=new Processo("systemd", 14, QUANTUM + 250);
        Processo p7=new Processo("discord", 9, QUANTUM - 50);

//        prontos.addAll(List.of(p1, p4, p2, p3));
        prontosRR.addAll(List.of(p1, p4, p2, p3));

        /*Timer x = new Timer();
        TimerTask task = new TimerTask() {
            Processo escolhido = null;

            @Override
            public void  run() {
                if (prontos.size() <= 1 && entrada_plus){
                    prontos.addAll(List.of(p5,p6,p7));
                    entrada_plus = false;
                }

//                mostrarStatus(escolhido);
                desbloqueio(escolhido);

                if (escolhido == null){
                    if (!prontos.isEmpty()) {
                        escolhido=prontos.poll();
                        relogio=escolhido.getQuantum();
                    }
                    else if(prontos.isEmpty() && bloqueados.isEmpty()){
                        System.out.print("Nenhum processo sendo executado");
                        System.out.println("\n\n---------------------------------------------------------------------------------------------------------");
                        System.out.println("terminados : " + finalizados);
                        System.out.println("Prontos: " + prontos);
                        System.out.println("Bloqueados: " +bloqueados);
                        System.out.println("\n\n");
                        System.out.printf("A média do tempo de espera dos processos é de %3.2f milisegundos",fazerMedia());
                        x.cancel();
                    }
                } else{
                    if (escolhido.getEstado() == Estados.FINALIZADO){
                        var buffer = escolhido;
                        buffer.toFinalizado();
                        finalizados.add(buffer);
                        escolhido = prontos.poll();
                        if (escolhido != null)
                            relogio = escolhido.getQuantum();
                        mostrarStatus(escolhido);

                    }
                    else if (relogio <= 0){
                        if (prontos.isEmpty()) {
                            relogio = escolhido.getQuantum();
                            //como não existe nenhum processo na fila dos prontos, o processo continua com tempo no processador
                        }else{
                            var buffer = escolhido;
                            escolhido = prontos.poll();
                            buffer.toPronto();
                            prontos.add(buffer);
                            relogio = escolhido.getQuantum();
                            mostrarStatus(escolhido);
                        }
                    }
                    else if (escolhido.getEstado() == Estados.BLOQUEADO){
                        if (prontos.isEmpty()) {
                            bloqueados.add(escolhido);
                            escolhido = null;
                        }else{
                            var buffer = escolhido;
                            escolhido = prontos.poll();
                            buffer.toBloqueado();
                            bloqueados.add(buffer);
                            relogio = escolhido.getQuantum();
                        }
                        mostrarStatus(escolhido);
                    }

                    executaProcesso(escolhido);
                    mostrarStatus(escolhido);

                }
            }
            };

        x.scheduleAtFixedRate(task,0, TEMPO_CLOCK);*/

        Timer y = new Timer();
        TimerTask tasky = new TimerTask() {
            Processo escolhido=null;

            public void  run() {
                if (prontosRR.size() <= 1 && entrada_plus){
                    prontosRR.addAll(List.of(p5,p6,p7));
                    entrada_plus = false;
                }

//                mostrarStatus(escolhido);
                desbloqueioRR(escolhido);

                if (escolhido == null){
                    if (!prontosRR.isEmpty()) {
                        escolhido=prontosRR.poll();
                        relogio=escolhido.getQuantum();
                    }
                    else if(prontosRR.isEmpty() && bloqueados.isEmpty()){
                        System.out.print("Nenhum processo sendo executado");
                        System.out.println("\n\n---------------------------------------------------------------------------------------------------------");
                        System.out.println("terminados : " + finalizados);
                        System.out.println("Prontos: " + prontosRR);
                        System.out.println("Bloqueados: " +bloqueados);
                        System.out.println("\n\n");
                        System.out.printf("A média do tempo de espera dos processos é de %3.2f milisegundos",fazerMedia());
                        y.cancel();
                    }
                } else{
                    if (escolhido.getEstado() == Estados.FINALIZADO){
                        var buffer = escolhido;
                        buffer.toFinalizado();
                        finalizados.add(buffer);
                        escolhido = prontosRR.poll();
                        if (escolhido != null)
                            relogio = escolhido.getQuantum();
                        mostrarStatusRR(escolhido);

                    }
                    else if (relogio <= 0){
                        if (prontosRR.isEmpty()) {
                            relogio = escolhido.getQuantum();
                            //como não existe nenhum processo na fila dos prontos, o processo continua com tempo no processador
                        }else{
                            var buffer = escolhido;
                            escolhido = prontosRR.poll();
                            buffer.toPronto();
                            prontosRR.add(buffer);
                            relogio = escolhido.getQuantum();
                            mostrarStatusRR(escolhido);
                        }
                    }
                    else if (escolhido.getEstado() == Estados.BLOQUEADO){
                        if (prontosRR.isEmpty()) {
                            bloqueados.add(escolhido);
                            escolhido = null;
                        }else{
                            var buffer = escolhido;
                            escolhido = prontosRR.poll();
                            buffer.toBloqueado();
                            bloqueados.add(buffer);
                            relogio = escolhido.getQuantum();
                        }
                        mostrarStatusRR(escolhido);
                    }

                    executaProcessoRR(escolhido);
                    mostrarStatusRR(escolhido);

                }
            }
        };
        y.scheduleAtFixedRate(tasky,0,TEMPO_CLOCK);

        }

        public static void executaProcesso(Processo processo){
        if (processo != null){
                processo.toExec();

                processo.setTempoCPU(processo.getTempoCPU() + TEMPO_CLOCK);
                relogio-=TEMPO_CLOCK;
                if (gerador.nextInt(100) <= (PORCENTAGEM_TERMINO - 1)) {
                    processo.toFinalizado();
                    return;
                }
                if (gerador.nextInt(100) <= (PORCENTAGEM_BLOQUEIO - 1)) {
                    processo.toBloqueado();
                }
            }

            //Adiciona os tempos de espera para os processos que não estão executando
            for (Processo x: prontos) {
                x.addTempoEspera(TEMPO_CLOCK);
            }

            for (Processo x: bloqueados) {
                x.addTempoEspera(TEMPO_CLOCK);
            }

        }

    public static void desbloqueio(Processo escolhido){ // Tem chance de desbloqueador o primeiro processo da lista em %
        if (!bloqueados.isEmpty()){
            if (gerador.nextInt(100) <= PORCENTAGEM_DESBLOQUEIO) {
                Processo p = bloqueados.get(0);
                bloqueados.remove(0);
                p.setEstado(Estados.PRONTO);
                prontos.add(p);
                mostrarStatus(escolhido);
            }
        }
    }

    public static void desbloqueioRR(Processo escolhido){ // Tem chance de desbloqueador o primeiro processo da lista em %
        if (!bloqueados.isEmpty()){
            if (gerador.nextInt(100) <= PORCENTAGEM_DESBLOQUEIO) {
                Processo p = bloqueados.get(0);
                bloqueados.remove(0);
                p.setEstado(Estados.PRONTO);
                prontosRR.add(p);
                mostrarStatus(escolhido);
            }
        }
    }

    public static void executaProcessoRR(Processo processo){
        if (processo != null){
            processo.toExec();

            processo.setTempoCPU(processo.getTempoCPU() + TEMPO_CLOCK);
            relogio-=TEMPO_CLOCK;
            if (gerador.nextInt(100) <= (PORCENTAGEM_TERMINO - 1)) {
                processo.toFinalizado();
                return;
            }
            if (gerador.nextInt(100) <= (PORCENTAGEM_BLOQUEIO - 1)) {
                processo.toBloqueado();
            }
        }

        //Adiciona os tempos de espera para os processos que não estão executando
        for (Processo x: prontosRR) {
            x.addTempoEspera(TEMPO_CLOCK);
        }

        for (Processo x: bloqueados) {
            x.addTempoEspera(TEMPO_CLOCK);
        }

    }


    public static void mostrarStatus(Processo processo){
        if (processo != null){
            if (processo.getEstado() == Estados.PRONTO)
                System.out.print(ANSI_CIAN+"["+processo.getId()+"]"+ANSI_RST+" Troca: " + processo);
            else if(processo.getEstado() == Estados.BLOQUEADO)
                System.out.print(ANSI_RED_BACKGROUND+"["+processo.getId()+"]"+ANSI_RST+" Block: " + processo);
            else if (processo.getEstado() == Estados.FINALIZADO)
                System.out.print(ANSI_GREEN_BACKGROUND+  "\u001B[30m" +"["+processo.getId()+"]"+ANSI_RST+" Finish: " + processo);
            else
                System.out.print(ANSI_YELLOW+"["+processo.getId()+"]"+ANSI_RST+" Status: " + processo);
        }else
            System.out.print("Nenhum processo sendo executado\n");
        System.out.print(prontos.stream().map(Processo::getId).toList().toString() + "  |    ");
        System.out.print(ANSI_VERMELHO +bloqueados.stream().map(Processo::getId).toList().toString() + ANSI_RST+ "  |    ");
        System.out.print(ANSI_VERDE + finalizados.stream().map(Processo::getId).toList().toString() + ANSI_RST+ "  |    ");

    }

    public static void mostrarStatusRR(Processo processo){
        if (processo != null){
            if (processo.getEstado() == Estados.PRONTO)
                System.out.print(ANSI_CIAN+"["+processo.getId()+"]"+ANSI_RST+" Troca: " + processo);
            else if(processo.getEstado() == Estados.BLOQUEADO)
                System.out.print(ANSI_RED_BACKGROUND+"["+processo.getId()+"]"+ANSI_RST+" Block: " + processo);
            else if (processo.getEstado() == Estados.FINALIZADO)
                System.out.print(ANSI_GREEN_BACKGROUND+  "\u001B[30m" +"["+processo.getId()+"]"+ANSI_RST+" Finish: " + processo);
            else
                System.out.print(ANSI_YELLOW+"["+processo.getId()+"]"+ANSI_RST+" Status: " + processo);
        }else
            System.out.print("Nenhum processo sendo executado\n");
        System.out.print(prontosRR.stream().map(Processo::getId).toList().toString() + "  |    ");
        System.out.print(ANSI_VERMELHO +bloqueados.stream().map(Processo::getId).toList().toString() + ANSI_RST+ "  |    ");
        System.out.print(ANSI_VERDE + finalizados.stream().map(Processo::getId).toList().toString() + ANSI_RST+ "  |    ");

    }

    public static double fazerMedia(){
        OptionalDouble x = finalizados.stream().mapToLong(Processo::getTempoCPU).average();
        double a = x.getAsDouble();
        return a;
    }
}

