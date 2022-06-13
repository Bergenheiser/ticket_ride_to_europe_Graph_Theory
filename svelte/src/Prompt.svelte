<script>
    export let props;
    import { ws } from "./stores.js";

    function onClickButton(label) {
        $ws.send(label);
    }
</script>

<div class="prompt">
    <div class="instruction">
    <span class="nom-joueur">{props.nomJoueurCourant}:</span>
    {props.instruction}
    </div>
    <div class="boutons">
        {#each props.boutons as bouton}
            <button
                class="btn btn-primary"
                on:click={() => onClickButton(bouton)}
                >{bouton}</button
            >
        {/each}
        {#if props.peutPasser}
            <button on:click={() => $ws.send("")}>Passer</button>
        {:else}
            <button disabled>Passer</button>
        {/if}
    </div>
</div>

<style>
    .nom-joueur {
        font-weight: bold;
    }
</style>
