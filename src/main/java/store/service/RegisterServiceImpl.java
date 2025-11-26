package store.service;

import store.model.Register;
import java.util.ArrayList;
import java.util.List;

public class RegisterServiceImpl implements RegisterService {
    private final List<Register> registers;

    public RegisterServiceImpl() {
        this.registers = new ArrayList<>();
    }

    @Override
    public void addRegister(Register register) {
        if (register==null) {
            throw new IllegalArgumentException("registe cannot be null");
        }

        for (Register r : registers) {
            if (r.getId()==register.getId()) {
                throw new IllegalArgumentException("register with ID " + register.getId() + " already exists");
            }
        }

        registers.add(register);
    }

    @Override
    public Register findRegisterById(int registerId) {
        for (Register r : registers) {
            if (r.getId()==registerId) {
                return r;
            }
        }
        return null;
    }

    @Override
    public List<Register> getAllRegisters() {
        return new ArrayList<>(registers);
    }
}
