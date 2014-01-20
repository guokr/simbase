Simbase: A vector similarity database
======================================

Simbase is a redis-like vector similarity database. You can add, get, delete
vectors to/from it, and then retrieve the most similar vectors within one vector
set or between two vector sets.

Release
--------

Current version is [v0.1.0-alpha2](https://github.com/guokr/simbase/releases/tag/v0.1.0-alpha2).

Concepts
--------

Simbase use a concept model as below:

                       + - - - +
          +----------->| Basis |<------------------+
          |  belongs   + _ _ _ +      belongs      |
          |                                        |
          |                                        |
    + - - - - - +        source           + - - - - - - - -+ 
    | VectorSet |<------------------------| Recommendation |
    + - - - - - +                         + - - - - - - - -+
          ^              target                    |
          |________________________________________|

* Vector set: a set of vectors
* Basis: the basis for vectors, vectors in one vector set have same basis
* Recommendation: a one-direction binary relationship between two vector sets which have the same basis

A real example follow the model below:

         + - - - - - +                 + - - - - - - - -+ 
    +--->|  Articles |<----------------|  User Profiles |
    |    + - - - - - +                 + - - - - - - - -+
    |          |
    +----------+

This graph shows

* recommend article by article (recommend from article to article)
* recommend article by user profile (recommend from user profile to article)

Limitations
------------

### Assumptions on vectors

Although Simbase is a vector data store, it does not accept vectors without any constrains.
In fact, Simbase only accepts a vector which its components are all greater than zero and less than one.

Especially, if you adopt "jensenshannon" as your score function, you should assure your vector is a
probability distribution, i.e. the sum of all components equals to one.

### Performance consideration

The write operation is handled in a single thread per basis, and comparison between any two vectors is needed,
so the write operation is scaled at O(n).

We had a non-final performance test on an i7-cpu Macbook, it can easily handle 120k 1k-dimensional vectors
with each write operation in under 1 sec.

Since the data is all in memory, the read operation is pretty fast.

We are still in the process of tuning the performance.

How to build and start
-----------------------

To build the project, you need install leiningen first, and then

  > cd SIMBASE_HOME
  
  > lein uberjar

After the uberjar is created, you can start the system

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

*   rmk vecsetname1 vecsetname2 funcscore

    > rmk userprofile article cosinesq
    
    Create a recommendation to article by userprofile and it use cosinesq as score function.
    Currently score functions you can choice are: 'cosinesq' and 'jensenshannon' 

*   rrec vecsetname1 vecid vecsetname2

    > rrec userprofile 87654321 article
    
    Recommend articles for user 87654321

Licenses
---------

Simbase is dual licensed under the Apache License 2.0 and
Eclipse Public License 1.0. Simbase is free for commercial use
and distribution under the terms of either license.

Special thanks
---------------

Special thanks for Feng Sheng, we borrowed lots of code from his
great project http-kit ( https://github.com/http-kit/http-kit/ ).

Also thanks for Kunwei Zhang from Tsinghua Univ. for his smart idea.  

Contributors
-------------

* Mingli Yuan ( https://github.com/mountain )
* Wanjian Wu ( https://github.com/jseagull )
* Yang Zhang ( https://github.com/zmouren )
* Jianjiang Zhu ( https://github.com/zjjott )
* Jiacai Liu ( https://github.com/jiacai2050 )




