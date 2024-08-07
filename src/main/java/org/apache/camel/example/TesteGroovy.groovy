package org.apache.camel.example


class Teste1 {

    String[] values = new String[100]

    String getAt(int idx) {
        return values[idx]
    }

    void putAt(int idx, String value) {
        values[idx] = value
    }
}

class Teste2 {
    def mapa = [nome: 'João', idade: 30, cidade: 'São Paulo']

    String getAt(String key) {
        return mapa[key];
    }

    Teste2 putAt(String key, String value) {
        mapa[key] = value;
        return this
    }

    boolean containsKey(String key) {
        return mapa.hasProperty(key);
    }

    Teste2 remove(String key) {
        mapa.remove(key)
        return this;
    }

    int getSize() {
        return mapa.size();
    }

    void minus(String key) {
        mapa.remove(key)
    }


}



def teste1 = new Teste1();

teste1[0] = "osvaldo";

println teste1[0]

def teste2 = new Teste2();

teste2["nome"] = "osvaldo"
teste2.
println teste2.size
teste2 -= "nome"

teste2.each { chave, valor ->
  println "${chave}=${valor}"
}
println teste2?["nome"]

