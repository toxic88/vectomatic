Carballo Chess Engine
Alberto Alonso Ruibal <alberto@alonsoruibal.com>

Carballo is an Open Source Java chess engine with the following features:

* Simple and clear code (has 5000 lines aprox. excluding tests)
* Maven source code organization
* JUnit used for testing, multiple test suites provided (Perft, BT2630, LCTII, WAC, etc.)
* Based on Bitboards (not so complicated as other people say)
* State-of-the-art magic bitboard move generator (doubles the basic move generator speed!), also code for magic number generation
* Negascout or Negamax searcher (configurable)
* Iterative deepening
* Aspiration window, moves only one border of the window if falls out
* Transposition table with Zobrist Keys (two zobrist keys per board, to avoid collisions) and multiprobe/two tier
* Move sorting: pluggable sorter: two killer move slots, MVV/LVA and history heuristic
* Also Internal Iterative Deepening to improve sorting
* Search extensions: mate threat, check, pawn push
* Reductions: Late Move Reductions (LMR)
* Pruning: Null Move, Futility Pruning and Aggressive Futility Pruning (Futilty deactivated)
* Polyglot Opening Book support; in the code I include Fruit's Small Book
* FEN notation import/export support, also EPD support for testing
* Pluggable evaluator function, two distinct functions provided, the Simplified Evaluator Function and the Complete
* Cute drag and drop Applet GUI
* Also UCI interface

It scores aprox. 2100 ELO points in BT2450 or BT2630 tests in my Core2 Duo@2.2GHz.
Also solves (only) 237 positions of the 300 WinAtChess test (5 seconds for each), far away of other engines.

It is licensed under GPL, and you are free to use, distribute or modify the code but I ask for a mention to the original author and/or a link to my blog.
I also put links in my source code to webs were I found information for coding.

Evolution of its ELO:

* ELO 1500 Basic evaluator function (only material and PSVs), negamax with a very basic move sorting
* ELO 1700 Improved evaluation function, aspiration window
* ELO 1730 Pscq tables generation, Negascout Search, Late Move Reductions (LMR)
* ELO 1650 (backwards) Move format rewrite, move generation speeds up from 300.000 to 400.000 positions per second. Move sorting improved (killer heuristics, etc), negascout and LMR fails: deactivated
* ELO 1800 With simplified evaluator function. History heuristic without extensions, need to implement Quiescent Search (QS)
* ELO 1780 With complete evaluator function, quiescence search and aspiration window, now is time to negascout search and improve evaluation
* ELO 1880 With check extensions and LMR
* ELO < Futility pruning appear to improve nothing
* ELO 1900 with BT2630 push to 7th extensions
* ELO 2000 with BT2630 TT reorganization
* ELO 2050 IID + null move pruning (Carballo 0.1)
* ELO 2100 Phased move generator: refactored move sorting (Carballo 0.2)

TO-DO

* Negascout increases nodes searched: must improve move ordering to use negascout (maybe SSE?)
* Futility pruning reduces nodes but sometimes loses important variants
* Change sign when thinking in black
* Ponder
* Improve evaluator
* UCI Options
* When parsing a move that does not need to be disambiaguated beause one of the options isn't legal, the program fails. 

I made a Java Engines Tournament to compare Carballo against other chess engines at tournament time 5 minutes. Here are the results: 

    Engine        Score      Al    Me    Br    Ca    Ca    Ar    Ch    Fr    Ta    JC    S-B
01: Alf109        41,5/45 ····· 111=0 1=1=1 11111 11==1 11111 11111 11111 11111 11111  804,50
02: Mediocre_034  39,0/45 000=1 ····· 11111 0=111 11111 11111 11111 11111 11111 11101  737,00
03: Bremboce      29,0/45 0=0=0 00000 ····· =011= 100=1 10111 =1111 =1111 1=111 11111  460,00
04: Carballo-0.2  27,5/45 00000 1=000 =100= ····· 01011 =100= 1=111 1=111 11111 11111  419,75
05: Carballo-0.1  25,5/45 00==0 00000 011=0 10100 ····· 11==0 0111= 1111= =1111 11=11  397,00
06: ArabianKnight 24,5/45 00000 00000 01000 =011= 00==1 ····· 11=10 11111 11111 11111  327,75
07: Cheoss0649    16,5/45 00000 00000 =0000 0=000 1000= 00=01 ····· 111== 11110 111=1  192,50
08: Frittle-0.3   8,0/45  00000 00000 =0000 0=000 0000= 00000 000== ····· 11001 1=0==   94,75
09: Talvmenni     7,0/45  00000 00000 0=000 00000 =0000 00000 00001 00110 ····· =0=11   79,25
10: JChecs        6,5/45  00000 00010 00000 00000 00=00 00000 000=0 0=1== =1=00 ·····   94,00

225 games played / Tournament is finished
Level: Tournament Game in 5 Minutes
Hardware: Intel(R) Core(TM)2 Duo CPU     T7500  @ 2.20GHz 2200 MHz with 752 MB Memory
Operating system: Microsoft Windows XP Professional Service Pack 2 (Build 2600)
