package mhd.sosrota.controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import mhd.sosrota.infrastructure.AppContext;
import mhd.sosrota.model.Profissional;
import mhd.sosrota.model.enums.FuncaoProfissional;
import mhd.sosrota.model.exceptions.CadastroException;
import mhd.sosrota.model.exceptions.DeleteException;
import mhd.sosrota.navigation.Navigable;
import mhd.sosrota.navigation.Navigator;
import mhd.sosrota.presentation.UiUtils;
import mhd.sosrota.service.ProfissionalService;
import mhd.sosrota.util.AlertUtil;

import java.sql.SQLException;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 01/12/2025
 * @brief Class EditarProfissionalController
 */
public class EditarProfissionalController implements Navigable {
    @FXML
    private Button salvarModalButton, cancelarButton;
    @FXML
    private ComboBox<String> funcaoComboBox;
    @FXML
    private TextField nomeField, contatoField;
    @FXML
    private Label erroLabel;

    private final ProfissionalService service = AppContext.getInstance().getProfissionalService();
    private Profissional profissionalEmEdicao;
    private Navigator navigator;

    @FXML
    private void initialize() {
        erroLabel.setVisible(false);
        erroLabel.setManaged(false);

        funcaoComboBox.getItems().addAll(FuncaoProfissional.getNomes());
        profissionalEmEdicao = AppContext.getInstance().getProfissionalEmEdicao();
        nomeField.setText(profissionalEmEdicao.getNome());
        contatoField.setText(profissionalEmEdicao.getContato());
        funcaoComboBox.getSelectionModel().select(profissionalEmEdicao.getFuncaoProfissional().getNome());

        salvarModalButton.disableProperty().bind(
                nomeField.textProperty().isEqualTo(profissionalEmEdicao.getNome())
                        .and(contatoField.textProperty().isEqualTo(profissionalEmEdicao.getContato()))
                        .and(funcaoComboBox.valueProperty().isEqualTo(profissionalEmEdicao.getFuncaoProfissional().getNome()))
        );
    }

    @FXML
    private void handleSalvar() {
        erroLabel.setVisible(false);
        erroLabel.setManaged(false);

        String nome = nomeField.getText().trim();
        String contato = contatoField.getText().trim();
        String funcao = funcaoComboBox.getValue();

        UiUtils.setButtonLoading(salvarModalButton, true, "Salvar alterações");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                service.atualizarProfissional(profissionalEmEdicao.getId(), nome, funcao, contato);
                return null;
            }

            @Override
            protected void succeeded() {
                UiUtils.setButtonLoading(salvarModalButton, false, "Salvar alterações");
                AlertUtil.showInfo("Sucesso", "Profissional editado com sucesso!");
                handleCancelar();
            }

            @Override
            protected void failed() {
                UiUtils.setButtonLoading(salvarModalButton, false, "Salvar alterações");
                Throwable e = getException();

                if (e instanceof CadastroException) {
                    mostrarErro(e.getMessage());
                } else if (e instanceof SQLException) {
                    e.printStackTrace();
                    mostrarErro("Erro no sistema.");
                } else {
                    e.printStackTrace();
                    mostrarErro("Algo deu errado.");
                }
            }
        };

        new Thread(task).start();
    }

    @FXML
    private void handleCancelar() {
        AppContext.getInstance().setProfissionalEmEdicao(null);
        navigator.closeStage(cancelarButton);
    }

    @FXML
    private void handleDeletar() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                service.deletarProfissional(profissionalEmEdicao.getId());
                return null;
            }

            @Override
            protected void succeeded() {
                AlertUtil.showInfo("Sucesso", "Profissional deletado com sucesso.");
                handleCancelar();
            }

            @Override
            protected void failed() {
                Throwable e = getException();
                if (e instanceof DeleteException) {
                    AlertUtil.showError("Erro!", e.getMessage());
                } else {
                    mostrarErro("Erro ao deletar profissional.");
                }
            }
        };
        var result = AlertUtil.showConfirmation("Deletar profissional", "Tem certeza que deseja deletar esse profissional?");
        if (result.get() == ButtonType.OK) {
            new Thread(task).start();
        }
    }

    private void mostrarErro(String mensagem) {
        erroLabel.setText(mensagem);
        erroLabel.setVisible(true);
        erroLabel.setManaged(true);
    }

    @Override
    public void setNavigator(Navigator navigator) {
        this.navigator = navigator;
    }
}
