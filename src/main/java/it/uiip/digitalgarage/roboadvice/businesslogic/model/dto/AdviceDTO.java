package it.uiip.digitalgarage.roboadvice.businesslogic.model.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class AdviceDTO {

    private Long assetClassId;

    private Advice advice;

    public enum Advice {
        BUY_ASSET(1), MAINTAIN_ASSET(0), SELL_ASSET(-1);

        private final Integer code;

        @JsonValue
        public Integer getCode(){
            return this.code;
        }

        Advice(Integer code){
            this.code = code;
        }

    }
}
