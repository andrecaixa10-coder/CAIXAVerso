import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {

    static class Transacao implements Comparable<Transacao> {

        String titular;
        String operacao;
        String dataHoraTexto;
        LocalDateTime dataHora;
        double valor;
        String operador;

        public Transacao(String titular, String operacao, String dataHoraTexto, double valor, String operador) {

            this.titular = titular;
            this.operacao = operacao;
            this.dataHoraTexto = dataHoraTexto;
            this.valor = valor;
            this.operador = operador;

            DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            this.dataHora = LocalDateTime.parse(dataHoraTexto, formato);
        }

        public int compareTo(Transacao outra) {
            return this.dataHora.compareTo(outra.dataHora);
        }

        public boolean equals(Object obj) {

            if (!(obj instanceof Transacao)) {
                return false;
            }

            Transacao t = (Transacao) obj;

            return valor == t.valor &&
                    operacao.equals(t.operacao) &&
                    operador.equals(t.operador) &&
                    dataHora.equals(t.dataHora);
        }

        public int hashCode() {
            return (operacao + operador + dataHoraTexto + valor).hashCode();
        }
    }

    public static void main(String[] args) {

        String endereco = "https://raw.githubusercontent.com/andrecaixa10-coder/CAIXAVerso/refs/heads/main/transacoes_bancarias.csv";

        List<Transacao> lista = new ArrayList<>();

        try {

            URL url = new URL(endereco);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

            String linha = br.readLine();

            while ((linha = br.readLine()) != null) {

                String[] p = linha.split(",");

                String titular = p[3];
                String operacao = p[4];
                String dataHora = p[5];

                String valorTexto = p[6];

                if (p.length > 8) {
                    valorTexto = p[6] + "," + p[7];
                }

                valorTexto = valorTexto.replace("R$", "");
                valorTexto = valorTexto.replace("\"", "");
                valorTexto = valorTexto.replace(".", "");
                valorTexto = valorTexto.replace(",", ".");
                valorTexto = valorTexto.trim();

                double valor = Double.parseDouble(valorTexto);

                String operador = p[p.length - 1];

                Transacao t = new Transacao(
                        titular,
                        operacao,
                        dataHora,
                        valor,
                        operador
                );

                lista.add(t);
            }

            br.close();

        } catch (Exception e) {

            System.out.println("Erro: " + e.getMessage());
            return;

        }

        System.out.println("Total lido: " + lista.size());

        HashSet<Transacao> set = new HashSet<>(lista);
        List<Transacao> semDuplicadas = new ArrayList<>(set);

        System.out.println("Sem duplicadas: " + semDuplicadas.size());

        Collections.sort(semDuplicadas);

        Map<String, List<Transacao>> mapa = new HashMap<>();

        for (Transacao t : semDuplicadas) {

            if (!mapa.containsKey(t.titular)) {
                mapa.put(t.titular, new ArrayList<>());
            }

            mapa.get(t.titular).add(t);
        }

        for (String titular : mapa.keySet()) {

            System.out.println("\nTitular: " + titular);

            double saldo = 0;

            for (Transacao t : mapa.get(titular)) {

                System.out.printf("%s | %s | R$ %.2f%n", t.dataHoraTexto, t.operacao, t.valor);

                if (t.operacao.equals("DEPOSITO")) {
                    saldo += t.valor;
                } else {
                    saldo -= t.valor;
                }
            }

            System.out.printf("Saldo final: R$ %.2f%n", saldo);
        }
    }
}