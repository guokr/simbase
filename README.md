Simbase: A vector similarity database
======================================

Simbase is a redis-like vector similarity database. You can add, get, delete
vectors to/from it, and then retrieve the most similar vectors within one vector
set or among two vector set.

Concepts
--------

Simbase use a concept model as below:

                       | - - - |
          ------------>| Basis |<------------------|
          |  belongs   | _ _ _ |      belongs      |
          |                                        |
          |                                        |
    | - - - - - |        source           | - - - - - - - -| 
    | VectorSet |<------------------------| Recommendation |
    | - - - - - |                         | - - - - - - - -|
          ^              target                    |
          |________________________________________|


* Vector set: a set of vectors
* Basis: the basis for vectors, vectors in one vector set have same basis
* Recommendation: a one-direction binary relationship between two vector set which have the same basis

A real example follow this model is as below:

         | - - - - - |                 | - - - - - - - -| 
    |--->|  Articles |<----------------|  User Profiles |
    |    | - - - - - |                 | - - - - - - - -|
    |          |
    -----------|

This graph shows

* recommend article by article (recommend from article to article)
* recommend article by user profile (recommend from user profile to article)

How to start
-------------

  > cd SIMBASE_HOME
  
  > bin/start

Core commands
--------------
Then you can use redis-cli to connect to simbase directly

Basis related

*   blist

    > blist
    
    List all basis in system

*   bmk basisname components...

    > bmk b512 universe time space human animal plant...
    
    Create a basis
	
*   brev basisname components...

    > brev b512 plant animal human space time universe...
    
    Revise a basis
    
Vector set related

*   vlist basisname

    > vlist b512
    
    List all vector set with one basis

*   vmk basisname vecsetname

    > vmk b512 article
    
    Create a vector set

*   vget vecsetname vecid

    > vget article 12345678
    
    Get the vector for the article with id 12345678

*   vset vecsetname vecid components...

    > vset article 12345678 0.1 0.12 0.123 0.1234 0.12345 0.123456...
    
    set the value for the article vector with id 12345678

*   vacc vecsetname vecid components...

    > vacc article 12345678 0.1 0.12 0.123 0.1234 0.12345 0.123456...
    
    accumulate the value for the article vector with id 12345678

*   vrem vecsetname vecid

    > vrem article 12345678
    
   remove the vector with id 12345678 from article vector set 

Recommendation related

*   rlist vecsetname

    > rlist article
    
    List all recommendation targets with the inputed vecset as source

*   rmk vecsetname1 vecsetname2

    > rmk userprofile article
    
    Create a recommendation to article by userprofile

*   rrec vecsetname1 vecid vecsetname2

    > rrec userprofile 87654321 article
    
    Recommend articles for user 87654321




