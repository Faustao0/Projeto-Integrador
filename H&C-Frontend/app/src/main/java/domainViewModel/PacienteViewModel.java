package domainViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import domain.Paciente;
import controller.PacienteController;

public class PacienteViewModel extends ViewModel {

    private MutableLiveData<Paciente> paciente;
    private PacienteController repository;

    public PacienteViewModel() {
        paciente = new MutableLiveData<>();
        repository = new PacienteController();
    }

    public MutableLiveData<Paciente> getPaciente() {
        return paciente;
    }

    public void searchPatientByName(String name) {
        repository.getPatientByName(name).observeForever(new Observer<Paciente>() {
            @Override
            public void onChanged(Paciente result) {
                paciente.setValue(result);
            }
        });
    }

    public void searchPatientById(Long id) {
        repository.getPatientById(id).observeForever(result -> {
            paciente.setValue(result);
        });
    }
}