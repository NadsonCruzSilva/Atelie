package javafxmvc.utils;

public class ValidadorUtil {

    // Valida se a string contém apenas letras (com acentos) e espaços, e no mínimo 3 caracteres.
    public static boolean isNomeValido(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return false;
        }
        // Aceita letras de A-Z, a-z, caracteres acentuados e espaços.
        return nome.matches("^[a-zA-ZÀ-ÿ\\s]{3,}$");
    }

    // Valida se a string representa um CPF, removendo pontuações e verificando tamanho numérico (apenas formato, sem matemática).
    public static boolean isCpfValido(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return false;
        }
        String cleanCpf = cpf.replaceAll("[^0-9]", "");
        return cleanCpf.length() == 11;
    }

    // Valida se a string representa um CNPJ, removendo pontuações e verificando tamanho numérico (apenas formato, sem matemática).
    public static boolean isCnpjValido(String cnpj) {
        if (cnpj == null || cnpj.trim().isEmpty()) {
            return false;
        }
        String cleanCnpj = cnpj.replaceAll("[^0-9]", "");
        return cleanCnpj.length() == 14;
    }

    // Valida telefone garantindo que tenha no mínimo 10 e máximo 11 dígitos numéricos
    public static boolean isTelefoneValido(String telefone) {
        if (telefone == null || telefone.trim().isEmpty()) {
            return false;
        }
        String cleanTel = telefone.replaceAll("[^0-9]", "");
        return cleanTel.length() >= 10 && cleanTel.length() <= 11;
    }

    // Verifica se um valor double (como R$) é estritamente maior que zero e formato correto
    public static boolean isValorPositivo(String valorStr) {
        try {
            double valor = Double.parseDouble(valorStr.replace(",", "."));
            return valor > 0;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }
    
    // Verifica se um valor inteiro (como quantidade ou prazo) é estritamente maior que zero
    public static boolean isInteiroPositivo(String valorStr) {
        try {
            int valor = Integer.parseInt(valorStr);
            return valor > 0;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }
}
