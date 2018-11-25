package info.zelazko.minibank.controller.response;

import info.zelazko.minibank.persistance.model.Transfer;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import java.util.Optional;

@Value
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TransferVM extends ViewModel {
    private String uuid;
    private String source;
    private String destination;
    private int amount;
    private String currency;
    private Transfer.Status status;

    @Builder(toBuilder = true)
    public TransferVM(String uuid, String source, String destination, int amount, String currency, Transfer.Status status, ErrorResponse error) {
        super(error);
        this.uuid = uuid;
        this.source = source;
        this.destination = destination;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
    }

    public TransferVM(Transfer transfer) {
        super(null);
        this.uuid = transfer.getUuid();
        this.source = transfer.getSource();
        this.destination = transfer.getDestination();
        this.amount = transfer.getAmount();
        this.currency = Optional.ofNullable(transfer.getCurrency()).map(c -> c.getCurrencyCode()).orElse(null);
        this.status = transfer.getStatus();

    }
}
