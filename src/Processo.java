import java.util.concurrent.TimeUnit;

/*
* 2. Um "mini" simulador (pode se basear nessa ferramenta: https://sourceforge.net/projects/oscsimulator/) de escalonamento preemptivo de processos, onde seja possível um usuário (não precisa de interface gráfica, pode ser linha de comando):

Criar processos indicando: ID, Nome, prioridade, processo I/O bound ou CPU/bound, tempo de CPU total (ex.: em unidades inteiras de tempo, por exemplo, 1 a 10 ms). A cada criação, o processo deve ser inserido na fila de "pronto" para ser escalonado conforme algoritmo de escalonamento;
Escolher uma de duas opções de algoritmo de escalonamento implementadas (se em dupla escolher uma por integrante);
Selecionar o tempo de quantum da preempção (ex.: em unidades inteiras de tempo, por exemplo, 1 a 10 ms)
Mostrar a lista de processos na fila de "prontos" dinamicamente (atualizar conforme escalonamento);
Iniciar a execução e escalonamento de processos, mostrando (com logs, prints, graficamente, etc.) ao usuário qual processo está ativo na CPU (por quanto tempo), a preempção do processo e quais estão aguardando, indicando sempre a ordem de execução dos algoritmos.
Ao final da execução, indicar o tempo de turnaround de cada processo e o tempo médio de espera de todos os processos.*/
public class Processo implements Comparable<Processo>{

    private static int guardaId=1;

    private int id;
    private String nome;
    private int prioridade;
    private Long tempoCPU;
    private Estados estado;
    private long quantum;
    private long tempoEspera;

    public Processo() {
    }

    public Processo(String nome, int prioridade,long quantum)  {
        this.id= guardaId++;
        this.nome=nome;
        this.prioridade=prioridade;
        this.estado=Estados.PRONTO;
        this.quantum = quantum;
        this.tempoCPU = 0L;
        this.tempoEspera = 0L;
    }

    public long getTempoEspera() {
        return tempoEspera;
    }

    public void setTempoEspera(long tempoEspera) {
        this.tempoEspera=tempoEspera;
    }

    public Processo addTempoEspera(long tempoEspera){
        this.tempoEspera+= tempoEspera;
        return this;
    }

    public long getQuantum() {
        return quantum;
    }

    public void setQuantum(long quantum) {
        this.quantum=quantum;
    }

    public Estados getEstado() {
        return estado;
    }

    public void setEstado(Estados estado) {
        this.estado=estado;
    }

    public static int getGuardaId() {
        return guardaId;
    }

    public static void setGuardaId(int guardaId) {
        Processo.guardaId=guardaId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id=id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome=nome;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(int prioridade) {
        this.prioridade=prioridade;
    }

    public Long getTempoCPU() {
        return tempoCPU;
    }

    public void setTempoCPU(Long tempoCPU) {
        this.tempoCPU=tempoCPU;
    }

    @Override
    public String toString() {
        return  "id=" + id +
                ", nome='" + nome + '\'' +
                ", prioridade=" + prioridade +
                ", tempoCPU=" +  tempoCPU  +
                ", estado=" + estado +
                ", tempoEspera=" + tempoEspera
                +"\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Processo processo=(Processo) o;

        return id == processo.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public void toPronto(){
        this.estado = Estados.PRONTO;
    }

    public void toBloqueado(){
        this.estado=Estados.BLOQUEADO;
    }

    public void toExec(){
        this.estado=Estados.EXECUTANDO;
    }

    public void toFinalizado(){
        this.estado=Estados.FINALIZADO;
    }


    @Override
    public int compareTo(Processo o) {
        return this.prioridade - o.getPrioridade();
    }
}
