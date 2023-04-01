package ai.openfabric.api.controller;

import ai.openfabric.api.config.DockerClientConfig;
import ai.openfabric.api.model.Worker;
import ai.openfabric.api.repository.WorkerRepository;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${node.api.path}/worker")
public class WorkerController {

    @Autowired
    private WorkerRepository repository;
    @Autowired
    private DockerClientConfig dockerClientConfig;

    // to sync the database with the current state of the containers
    private void updateDatabase() {
        List<Container> containers = dockerClientConfig.dockerClient().listContainersCmd().exec();
        for (Container container : containers) {
            Worker worker = Worker.builder()
                    .id(container.getId())
                    .name(container.getNames()[0])
                    .state(container.getState())
                    .status(container.getStatus())
                    .containerId(container.getId())
                    .image(container.getImage())
                    .imageId(container.getImageId())
                    .build();
            container.getPorts();
            repository.save(worker);
        }
    }


    // displays all workers
    @GetMapping(path = "/get_workers")
    public @ResponseBody String getWorkers() {
        updateDatabase();
        return repository.findAll().toString();
    }


    // starts a worker given its ID in the body
    @PostMapping(path = "/start")
    public @ResponseBody String startWorker(@RequestBody String workerId) {
        try {
            dockerClientConfig.dockerClient().startContainerCmd(workerId).exec();
        }
        catch (Exception e) {
            return e.toString();
        }
        return "Success";
    }


    // stops a worker given its ID in the body
    @PostMapping(path = "/stop")
    public @ResponseBody String stopWorker(@RequestBody String workerId) {
        try {
            dockerClientConfig.dockerClient().stopContainerCmd(workerId).exec();
        }
        catch (Exception e) {
            return e.toString();
        }
        return "Success";
    }


    // gets info of the client
    @GetMapping(path = "/get_info")
    public @ResponseBody String getInfo() {
        Info info = dockerClientConfig.dockerClient().infoCmd().exec();
        return info.toString();
    }
}
