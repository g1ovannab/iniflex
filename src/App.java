import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class App {
    static List<Funcionario> funcionarios = new ArrayList<Funcionario>();
    static Map<String, List<Funcionario>> funcionariosPorFuncao;
    public static void main(String[] args) throws Exception {

        //Na especificação não ficou claro se as opções devem ser sequenciais e automáticas ou não. Criei um menu para seguir todos os pontos
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        
        loadFuncionarios();

        while (!exit) {
            // Exibe o menu
            System.out.println("Menu:");
            System.out.println("1. Remover João");
            System.out.println("2. Imprimir todos os funcionários");
            System.out.println("3. Atualizar funcionários com 10% de aumento no salário");
            System.out.println("4. Agrupar funcionários por função");
            System.out.println("5. Imprimir os funcionários agrupados por função");
            System.out.println("6. Imprimir os funcionários aque fazem aniversário em outubro e dezembro");
            System.out.println("7. Imprimir o funcionário com maior idade");
            System.out.println("8. Imprimir os funcionários em ordem alfabética");
            System.out.println("9. Imprimir o total dos salários");
            System.out.println("10. Imprimir quantos salários mínimos cada funcionário ganha");
            System.out.println("11. Sair");
            System.out.print("Escolha uma opção: ");

            // Lê a escolha do usuário
            int escolha = scanner.nextInt();
            scanner.nextLine(); // Consumir nova linha para evitar problemas com nextLine()

            // Processa a escolha
            switch (escolha) {
                case 1:
                    removerFuncionario("João"); break;
                case 2:
                    for (Funcionario funcionario : funcionarios) {
                        System.out.println(funcionario.toString());
                    }; break;
                case 3: 
                    atualizarSalario(10, true); break;
                case 4: 
                    funcionariosPorFuncao = funcionarios.stream().collect(Collectors.groupingBy(Funcionario::getFuncao));
                    break;
                case 5: 
                    funcionariosPorFuncao.forEach((funcao, lista) -> {
                        System.out.println("Função: " + funcao);
                        for (Funcionario funcionario : lista) {
                            System.out.println(funcionario.toString());
                        }
                        System.out.println();
                    });    
                    break;
                case 6: 
                    for (Funcionario funcionario : funcionarios) {
                        if (funcionario.getDataNascimento().getMonth() == Month.OCTOBER || funcionario.getDataNascimento().getMonth() == Month.DECEMBER) System.out.println(funcionario.toString());
                    }; break;
                case 7: 
                    System.out.println(funcionarios.stream().max(Comparator.comparingInt(Funcionario::getIdade)).orElse(null).toStringNomeIdade());
                    break;
                case 8: 
                    List<Funcionario> copiaFuncionarios = new ArrayList<>(funcionarios);
                    Collections.sort(copiaFuncionarios, (f1, f2) -> f1.getNome().compareTo(f2.getNome()));
                    for (Funcionario funcionario : copiaFuncionarios) {
                        System.out.println(funcionario.toString());
                    }; break;
                case 9: 
                    BigDecimal totalSalarios = BigDecimal.ZERO;
                    for (Funcionario funcionario : funcionarios) {
                        totalSalarios = totalSalarios.add(funcionario.getSalario());
                    }
                    System.out.println(totalSalarios); break;
                case 10: 
                    BigDecimal salarioMinimo = new BigDecimal("1212.00");
                    for (Funcionario funcionario : funcionarios) {
                        BigDecimal salario = funcionario.getSalario();
                        BigDecimal quantSalariosMinimos = salario.divide(salarioMinimo, 2, RoundingMode.DOWN);
                        DecimalFormat df = new DecimalFormat("#,##0.00");
                        System.out.println(funcionario.getNome() + ": " + df.format(quantSalariosMinimos) + " salários mínimos");
                    }    
                    break;
                case 11:
                    exit = true;
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }

            System.out.println();
        }
        scanner.close();
    }

    private static void loadFuncionarios(){
        String arquivoFuncionarios = ".//src//employees//employees.csv";
        String linha = "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try (BufferedReader br = new BufferedReader(new FileReader(arquivoFuncionarios, StandardCharsets.UTF_8))) {
            br.readLine(); //header
            while ((linha = br.readLine()) != null) {
                String[] valores = linha.split("\t");
                try {
                    funcionarios.add(new Funcionario(valores[0], LocalDate.parse(valores[1], formatter), new BigDecimal(valores[2]), valores[3]));
                } catch (Exception e) {
                    System.out.println("Erro na hora de converter algum valor: " + linha);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void atualizarSalario(int porcentagem, boolean aumento){
        for (Funcionario funcionario : funcionarios) {
            funcionario.setSalario(funcionario.getSalario().multiply(new BigDecimal("1.10")));
        }
    }

    private static void removerFuncionario(String nome){
        funcionarios.remove(funcionarios.stream().filter(p -> p.getNome().equals(nome)).findFirst().orElse(null));
    }

    private static void printFuncionarios(boolean selectAll){
        for (Funcionario funcionario : funcionarios) {
            System.out.println(selectAll ? funcionario.toString() : funcionario.toStringNomeIdade());
        }
    }
}
