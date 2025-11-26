package store.service;

import store.model.Register;
import java.util.List;

public interface RegisterService {
    void addRegister(Register register);
    Register findRegisterById(int registerId);
    List<Register> getAllRegisters();
}
