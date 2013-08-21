Simbase: A vector similarity database
======================================

Simbase is a redis-like vector similarity database. You can add, get, delete
vectors to/from it, and then retrive the most similar vectors with one.

We adopt cosine similarity as our distance function
* http://en.wikipedia.org/wiki/Cosine_similarity


How to start
-------------

  > cd SIMBASE_HOME
  
  > bin/start

Client Commands
----------------

Then you can use redis-cli to connect to simbase directly

*   vadd doctype docid probs: Add a document specified by the doctype, docid and probabilities

    > vadd article 123456 0.1 0.2 0.3 0.4
	
*   vget doctype docid: Get the probabilities of a document specified by the doctype and docid

    > vget article 123456
	
*   vput doctype docid probs: update the probabilities of a document specified by the doctype and docid

	> vput article 123456 0.4 0.3 0.2 0.1
	
*   vdel doctype docid: Delete a document specified by the doctype and docid

    > vdel article 123456
	
*   vretr doctype docid: Retrieve some similar documents of a document specified by the doctype and docid

    > vretr article 123456







