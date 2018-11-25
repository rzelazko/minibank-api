package info.zelazko.minibank.controller.response;

import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@NonFinal
public class ViewModel {
    protected final ErrorResponse error;
}
