package net.formula97.webapp.pims.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import net.formula97.webapp.pims.domain.IssueLedger;

/**
 * @author f97one
 *
 */
@Repository
public interface IssueLedgerRepository extends JpaRepository<IssueLedger, Integer>, JpaSpecificationExecutor<IssueLedger> {

    /*
     * @Query アノテーションは普通のSQLではなくJPQL(Java Persistence Query Language) で書く必要が
     * ある。
     * SQLはロー/カラム形式のテーブルとして結果が返るが、JPQLはオブジェクトベースなので、基本的にエンティティのコレクションで
     * 結果が返る。
     * また、JPQLは大文字/小文字を基本的に区別しないが、クラス/フィールドの名称はすべて元となるクラスの定義に従って
     * 書く必要があることに注意。
     */
    
    @Query("SELECT o FROM IssueLedger o WHERE o.isPublic = true ORDER BY ledgerId")
    List<IssueLedger> findByPublicLedger();
    
    @Query("SELECT o FROM IssueLedger o , LedgerRefUser u WHERE o.ledgerId = u.ledgerId AND u.userId = :userId")
    List<IssueLedger> findForUser(String userId);
}
