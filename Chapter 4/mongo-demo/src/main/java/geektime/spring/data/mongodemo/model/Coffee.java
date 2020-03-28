package geektime.spring.data.mongodemo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.money.Money;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document   // 与@Entity注解类似，在Mongo中标识一个文档
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coffee {
    // Mongo使用ID字段对表进行自动分割
    @Id
    private String id;
    private String name;
    private Money price;
    private Date createTime;
    private Date updateTime;
}
