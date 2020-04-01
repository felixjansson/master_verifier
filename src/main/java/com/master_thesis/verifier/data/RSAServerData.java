package com.master_thesis.verifier.data;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;

public class RSAServerData extends ComputationData {

    private BigInteger partialResult;
    private Map<Integer, ProofData> partialProofs;

    public RSAServerData() {
        super(Construction.RSA);
    }

    public BigInteger getPartialResult() {
        return partialResult;
    }

    public void setPartialResult(BigInteger partialResult) {
        this.partialResult = partialResult;
    }

    public Map<Integer, ProofData> getPartialProofs() {
        return partialProofs;
    }

    public void setPartialProofs(Map<Integer, ProofData> partialProofs) {
        this.partialProofs = partialProofs;
    }

    @Override
    public String toString() {
        return "RSAServerData{" +
                "partialResult=" + partialResult +
                ", RSAProofData=" + partialProofs +
                "} " + super.toString();
    }

    public static class ProofData {

        private BigInteger rsaN;
        private BigInteger[] rsaProofComponent;
        private double rsaDeterminant;
        private BigInteger clientProof;

        // This is not included in the data sent from the server but added later.
        private BigInteger publicKey;

        public BigInteger getRsaN() {
            return rsaN;
        }

        public void setRsaN(BigInteger rsaN) {
            this.rsaN = rsaN;
        }

        public BigInteger[] getRsaProofComponent() {
            return rsaProofComponent;
        }

        public void setRsaProofComponent(BigInteger[] rsaProofComponent) {
            this.rsaProofComponent = rsaProofComponent;
        }

        public double getRsaDeterminant() {
            return rsaDeterminant;
        }

        public void setRsaDeterminant(double rsaDeterminant) {
            this.rsaDeterminant = rsaDeterminant;
        }

        public BigInteger getClientProof() {
            return clientProof;
        }

        public void setClientProof(BigInteger clientProof) {
            this.clientProof = clientProof;
        }

        public BigInteger getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(BigInteger publicKey) {
            this.publicKey = publicKey;
        }

        @Override
        public String toString() {
            return "ProofData{" +
                    "rsaN=" + rsaN +
                    ", rsaProofComponent=" + Arrays.toString(rsaProofComponent) +
                    ", rsaDeterminant=" + rsaDeterminant +
                    ", clientProof=" + clientProof +
                    ", publicKey=" + publicKey +
                    '}';
        }
    }
}
