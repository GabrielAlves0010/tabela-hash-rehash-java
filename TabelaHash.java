import java.util.Random;

// Tabela hash de 100 posicoes com rehash linear e limite de 90% de ocupacao
public class TabelaHash {

    static final int TAMANHO = 100;
    static final int LIMITE_OCUPACAO = 90; // 90% de 100
    static final int TENTATIVAS = 100;
    static final int VAZIO = -1; // marca posicao livre

    // tabela hash e os dados de cada elemento inserido (valor, colisoes e posicao final)
    static int[] tabela = new int[TAMANHO];
    static int[] valoresInseridos = new int[TAMANHO];
    static int[] colisoesInseridos = new int[TAMANHO];
    static int[] posicoesInseridos = new int[TAMANHO];
    static int ocupados = 0;
    static int totalColisoes = 0;
    static int posicaoFinalDaUltimaInsercao = 0;
    static int sondagensDaUltimaBusca = 0;

    // funcao hash: resto da divisao por 100
    static int hash(int chave) {
        return chave % TAMANHO;
    }

    // rehash linear: vai para a proxima posicao e volta ao inicio depois da ultima
    static int rehash(int posicao) {
        return (posicao + 1) % TAMANHO;
    }

    // verifica se o valor ja esta na tabela para nao inserir repetido
    static boolean existeNaTabela(int chave) {
        for (int i = 0; i < TAMANHO; i++) {
            if (tabela[i] == chave) {
                return true;
            }
        }
        return false;
    }

    // insere a chave e devolve quantas colisoes ela teve ate achar posicao livre
    static int inserir(int chave) {
        int posicao = hash(chave);
        int colisoes = 0;
        while (tabela[posicao] != VAZIO) {
            colisoes++;
            posicao = rehash(posicao);
        }
        tabela[posicao] = chave;
        posicaoFinalDaUltimaInsercao = posicao;
        return colisoes;
    }

    // procura a chave seguindo o mesmo caminho da insercao
    // para quando acha a chave, acha uma posicao livre ou passa pela tabela inteira
    static int buscar(int chave) {
        int posicao = hash(chave);
        sondagensDaUltimaBusca = 0;
        for (int i = 0; i < TAMANHO; i++) {
            sondagensDaUltimaBusca++;
            if (tabela[posicao] == chave) {
                return posicao;
            }
            if (tabela[posicao] == VAZIO) {
                return -1;
            }
            posicao = rehash(posicao);
        }
        return -1;
    }

    // mostra a tabela com 10 posicoes por linha
    static void imprimirMatriz() {
        for (int linha = 0; linha < TAMANHO / 10; linha++) {
            String texto = "";
            for (int coluna = 0; coluna < 10; coluna++) {
                int posicao = linha * 10 + coluna;
                if (tabela[posicao] == VAZIO) {
                    texto = texto + posicao + ": LIVRE | ";
                } else {
                    texto = texto + posicao + ": " + tabela[posicao] + " | ";
                }
            }
            System.out.println(texto);
        }
    }

    // mostra quais posicoes ficaram livres
    static int imprimirPosicoesLivres() {
        int livres = 0;
        String texto = "";
        for (int i = 0; i < TAMANHO; i++) {
            if (tabela[i] == VAZIO) {
                livres++;
                texto = texto + i + " ";
            }
        }
        System.out.println("Posicoes livres (" + livres + "): " + texto);
        return livres;
    }

    // busca a chave e mostra o resultado
    static void testarBusca(int chave) {
        int posicao = buscar(chave);
        if (posicao >= 0) {
            System.out.println("Busca da chave " + chave + ": ENCONTRADA na posicao " + posicao
                    + " com " + sondagensDaUltimaBusca + " comparacoes (sondagens)");
        } else {
            System.out.println("Busca da chave " + chave + ": NAO ENCONTRADA, com "
                    + sondagensDaUltimaBusca + " comparacoes (sondagens)");
        }
    }

    public static void main(String[] args) {
        Random random = new Random(1090);

        // comeca com todas as posicoes livres
        for (int i = 0; i < TAMANHO; i++) {
            tabela[i] = VAZIO;
        }
        int bloqueados = 0;

        System.out.println("=== INSERCAO (seed 1090, tabela de " + TAMANHO + " posicoes, limite de "
                + LIMITE_OCUPACAO + " elementos) ===");

        for (int tentativa = 1; tentativa <= TENTATIVAS; tentativa++) {
            // sorteia de novo se o valor ja estiver na tabela
            int valor = random.nextInt(1000);
            while (existeNaTabela(valor)) {
                valor = random.nextInt(1000);
            }

            // com 90% ocupado, a insercao e bloqueada
            if (ocupados >= LIMITE_OCUPACAO) {
                bloqueados++;
                System.out.println("Tentativa " + tentativa + ": valor " + valor
                        + " BLOQUEADO pelo limite de 90% de ocupacao");
            } else {
                int hashInicial = hash(valor);
                int colisoes = inserir(valor);
                valoresInseridos[ocupados] = valor;
                colisoesInseridos[ocupados] = colisoes;
                posicoesInseridos[ocupados] = posicaoFinalDaUltimaInsercao;
                ocupados++;
                totalColisoes = totalColisoes + colisoes;
                System.out.println("Tentativa " + tentativa + ": valor " + valor + " | hash inicial "
                        + hashInicial + " | posicao final " + posicaoFinalDaUltimaInsercao
                        + " | colisoes " + colisoes);
            }
        }

        System.out.println();
        System.out.println("=== RELATORIO FINAL ===");
        System.out.println("Total de elementos inseridos: " + ocupados);
        System.out.println("Total de elementos bloqueados: " + bloqueados);
        System.out.println("Total de colisoes na insercao: " + totalColisoes);

        // acha os 3 elementos com mais colisoes, um de cada vez
        // em caso de empate fica o que foi inserido primeiro
        int[] indicesMaiores = new int[3];
        boolean[] jaEscolhido = new boolean[TAMANHO];
        for (int k = 0; k < 3; k++) {
            int melhor = -1;
            for (int i = 0; i < ocupados; i++) {
                if (!jaEscolhido[i]) {
                    if (melhor == -1 || colisoesInseridos[i] > colisoesInseridos[melhor]) {
                        melhor = i;
                    }
                }
            }
            jaEscolhido[melhor] = true;
            indicesMaiores[k] = melhor;
        }

        System.out.println();
        System.out.println("Os 3 elementos que mais colidiram:");
        for (int k = 0; k < 3; k++) {
            int i = indicesMaiores[k];
            System.out.println((k + 1) + "o lugar: valor " + valoresInseridos[i] + " | colisoes "
                    + colisoesInseridos[i] + " | posicao final " + posicoesInseridos[i]);
        }

        System.out.println();
        System.out.println("Matriz final:");
        imprimirMatriz();

        System.out.println();
        imprimirPosicoesLivres();

        // teste de busca com os 3 elementos que mais colidiram
        System.out.println();
        System.out.println("=== TESTE DE BUSCA ===");
        for (int k = 0; k < 3; k++) {
            testarBusca(valoresInseridos[indicesMaiores[k]]);
        }
        // busca de um valor que nao esta na tabela
        testarBusca(1000);
    }
}
