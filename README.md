# Spring boot - request on hold

Progetto dimostrativo spring boot. L'architettura si basa su `DeferredResult` (guarda
il [tutorial di baeldoung](https://www.baeldung.com/spring-deferred-result) per maggiori info).

L'obiettivo è tenere una richiesta REST in attesa fin quando non viene ricevuta una chiamata REST di unlock o comunque
fin quando non si raggiunge il tempo limite di time out

### Architettura

Il microservizio tiene in memoria tutte le richieste che sta tenendo in attesa, quando riceve la chiamata di unlock
riprende il `DeferredResult` della richiesta in questione e gli va a settare una risposta

Lo sviluppo è stato improntato alla conteinerizzazione del pacchetto. È possibile anche scalare il microservizio
orizzontalmente, infatti la propagazione dei messaggi per sbloccare le richieste viene fatta tramite database.
L'ambiente per cui è stato pensato questo progetto non possiede un modo per la propagazione di segnali JMS. Sarebbe la
soluzione ottimale perché si risparmierebbe il polling attivo sulla tabella per conoscere gli stati aggiornati

### Gli endpoint

Al momento ci sono solamente due endpoint: `/api/onhold/acquire` e `/api/onhold/unlock/{identifier}`. Non ho perso molto
tempo nella costruzione delle risposte. Qual ora si volesse aggiungere altre richieste è importante ricordare che il
microservizio che riceve la chiamata di `unlock` potrebbe non essere lo stesso che sta tenendo _on-hold_ la richiest,
quindi è necessario salvare la richiesta sul database (magari utilizzando la stessa chiave che permette di identificare
la richiesta. In questo progetto la chiave è `identifier`).

### Il timeout

La gestione del timeout è stata gestita in modo semplice grazie alla classe `DeferredResult`. Offre la possibilità di
creare un oggetto con già un timeout definito.