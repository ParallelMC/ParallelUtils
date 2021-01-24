package parallelmc.parallelutils.custommobs;

import org.bukkit.Particle;

public class ParticleData {
    public Particle particle;
    public int amount;
    public double hSpread;
    public double vSpread;
    public double speed;

    public ParticleData(Particle particle, int amount, double hSpread, double vSpread, double speed) {
        this.particle = particle;
        this.amount = amount;
        this.hSpread = hSpread;
        this.vSpread = vSpread;
        this.speed = speed;
    }
}
