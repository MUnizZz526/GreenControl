-- Oracle 19c / SQL*Plus ou SQLcl. EXECUÇÃO MANUAL, nunca pelo Spring.
-- Backup validado e janela de manutenção obrigatórios: parar TODOS os escritores,
-- inclusive console Sprint 3, imports e jobs, até concluir o deploy Sprint 4.
-- DDL Oracle faz COMMIT implícito. Em falha parcial, inspecionar USER_SEQUENCES;
-- não remover/recriar sequences nem reiniciar seus valores para tentar novamente.
-- Mantém tabelas, registros, FKs e colunas BY DEFAULT AS IDENTITY existentes.
-- Após a migração, somente escritores com os IDs destas sequences são permitidos.
-- O gerador IDENTITY antigo NÃO fica sincronizado: console/imports antigos estão
-- arquivados e NÃO devem gravar no schema migrado (risco de colisão).
WHENEVER SQLERROR EXIT SQL.SQLCODE
SET SERVEROUTPUT ON
DECLARE
    existentes NUMBER;
    inicio NUMBER;
    PROCEDURE criar(tabela VARCHAR2, coluna VARCHAR2, sequencia VARCHAR2) IS
    BEGIN
        EXECUTE IMMEDIATE 'SELECT NVL(MAX(' || coluna || '),0)+1 FROM ' || tabela INTO inicio;
        EXECUTE IMMEDIATE 'CREATE SEQUENCE ' || sequencia || ' START WITH ' ||
            TO_CHAR(inicio, 'TM9', 'NLS_NUMERIC_CHARACTERS=''.,''') ||
            ' INCREMENT BY 1 NOCYCLE NOCACHE';
        DBMS_OUTPUT.PUT_LINE(sequencia || ': criada acima do maior ID existente.');
    END;
BEGIN
    SELECT COUNT(*) INTO existentes FROM USER_SEQUENCES WHERE SEQUENCE_NAME IN
        ('SEQ_GC_EQUIPE','SEQ_GC_TRECHO','SEQ_GC_INTERVENCAO','SEQ_GC_RELATORIO');
    IF existentes > 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Sequence já existe: verificar migração anterior; nenhuma será reiniciada.');
    END IF;
    -- Preflight: falhar antes de criar qualquer sequence se faltar tabela/coluna.
    SELECT NVL(MAX(ID_EQUIPE),0) INTO inicio FROM T_EQUIPE_MANUTENCAO;
    SELECT NVL(MAX(ID_TRECHO),0) INTO inicio FROM T_TRECHO_RODOVIA;
    SELECT NVL(MAX(ID_INTERVENCAO),0) INTO inicio FROM T_INTERVENCAO_OPERACIONAL;
    SELECT NVL(MAX(ID_RELATORIO),0) INTO inicio FROM T_RELATORIO_PRIORIDADE;
    criar('T_EQUIPE_MANUTENCAO','ID_EQUIPE','SEQ_GC_EQUIPE');
    criar('T_TRECHO_RODOVIA','ID_TRECHO','SEQ_GC_TRECHO');
    criar('T_INTERVENCAO_OPERACIONAL','ID_INTERVENCAO','SEQ_GC_INTERVENCAO');
    criar('T_RELATORIO_PRIORIDADE','ID_RELATORIO','SEQ_GC_RELATORIO');
END;
/
