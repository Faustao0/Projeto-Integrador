package domainViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import domain.Medicamento;
import controller.MedicamentoController;

public class MedicamentoViewModel extends ViewModel {

    private MutableLiveData<Medicamento> medicamento;
    private MedicamentoController repository;

    public MedicamentoViewModel() {
        medicamento = new MutableLiveData<>();
        repository = new MedicamentoController();
    }

    public MutableLiveData<Medicamento> getMedicamento() {
        return medicamento;
    }

    public void searchMedicamentoByName(String name) {
        repository.getMedicamentoByName(name).observeForever(new Observer<Medicamento>() {
            @Override
            public void onChanged(Medicamento result) {
                medicamento.setValue(result);
            }
        });
    }
}
