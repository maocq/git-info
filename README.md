# Git Info  
  
## Errors
10000 DomainError  
20000 ValidationError  
30000 TechnicalError  

    10000 DomainError
        11000 HttpStatusError  

            11001 Error consultando proyecto
            11002 Error consultando commits
            11003 Error consultando diffs

            
        12000 QueryError
            12100 Not exist
                12101 Project no encontrado
            12200 Already exist
                12201 Project exist
                12202 Group exist
                
        13000 Updating


    20000 ValidationError

    30000 TechnicalError
